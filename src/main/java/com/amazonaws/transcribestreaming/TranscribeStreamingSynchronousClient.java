/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.amazonaws.transcribestreaming;

import software.amazon.awssdk.services.transcribestreaming.TranscribeStreamingAsyncClient;
import software.amazon.awssdk.services.transcribestreaming.model.LanguageCode;
import software.amazon.awssdk.services.transcribestreaming.model.MediaEncoding;
import software.amazon.awssdk.services.transcribestreaming.model.Result;
import software.amazon.awssdk.services.transcribestreaming.model.StartStreamTranscriptionRequest;
import software.amazon.awssdk.services.transcribestreaming.model.StartStreamTranscriptionResponseHandler;
import software.amazon.awssdk.services.transcribestreaming.model.TranscriptEvent;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * An example implementation of a simple synchronous wrapper around the async client
 */
public class TranscribeStreamingSynchronousClient {

    public static final int MAX_TIMEOUT_MS = 15 * 60 * 1000; //15 minutes

    private TranscribeStreamingAsyncClient asyncClient;
    private String finalTranscript = "";
    private String transcript = "";

    public TranscribeStreamingSynchronousClient(TranscribeStreamingAsyncClient asyncClient) {
        this.asyncClient = asyncClient;
    }

    public String transcribeFile(File audioFile) {
        try {
            int sampleRate = (int) AudioSystem.getAudioInputStream(audioFile).getFormat().getSampleRate();
            StartStreamTranscriptionRequest request = StartStreamTranscriptionRequest.builder()
                    .languageCode(LanguageCode.EN_US.toString())
                    .mediaEncoding(MediaEncoding.PCM)
                    .mediaSampleRateHertz(sampleRate)
                    .showSpeakerLabel(true)
                    .build();
            AudioStreamPublisher audioStream = new AudioStreamPublisher(new FileInputStream(audioFile));
            StartStreamTranscriptionResponseHandler responseHandler = getResponseHandler();
            System.out.println("launching request");
            CompletableFuture<Void> resultFuture = asyncClient.startStreamTranscription(request, audioStream, responseHandler);
            System.out.println("waiting for response, this will take some time depending on the length of the audio file");
            resultFuture.get(MAX_TIMEOUT_MS, TimeUnit.MILLISECONDS); //block until done
        } catch (IOException e) {
            System.out.println("Error reading audio file (" + audioFile.getName() + ") : " + e);
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            System.out.println("Error streaming audio to AWS Transcribe service: " + e);
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            System.out.println("Stream thread interupted: " + e);
            throw new RuntimeException(e);
        } catch (UnsupportedAudioFileException e) {
            System.out.println("File type not recognized: " + audioFile.getName() + ", error: " + e);
        } catch (TimeoutException e) {
            System.out.println("Stream not closed within timeout window of " + MAX_TIMEOUT_MS + " ms");
            throw new RuntimeException(e);
        }
        return finalTranscript;
    }

    /**
     * Get a response handler that aggregates the transcripts as they arrive
     * @return Response handler used to handle events from AWS Transcribe service.
     */
    private StartStreamTranscriptionResponseHandler getResponseHandler() {
        return StartStreamTranscriptionResponseHandler.builder()
                .subscriber(event -> {
                    List<Result> results = ((TranscriptEvent) event).transcript().results();
                    if(results.size()>0) {
                        Result firstResult = results.get(0);
                        if (firstResult.alternatives().size() > 0 &&
                                !firstResult.alternatives().get(0).transcript().isEmpty()) {
                            /*String transcript = firstResult.alternatives().get(0).transcript();*/
                            /* PR: getting the speaker info from the transcribe resuls */
                              transcript = firstResult.alternatives().get(0).transcript();

                              // Going to write a convoluted logic here to append all content element values for the transcript associated with a speaker
                              /*String speaker = "";
                              if(firstResult.isPartial()) {
                                speaker = " Speaker " + firstResult.alternatives().get(0).items().get(0).speaker() + ": ";
                                trans += firstResult.alternatives().get(0).transcript();
                                //transcript += firstResult.alternatives().get(0).items().get(0).content();
                              }*/
                              // End of the convoluted logic
                              //String transcript = "Speaker " + firstResult.alternatives().get(0).items().get(0).speaker() + ": " + firstResult.alternatives().get(0).items().get(0).content();
                            if(!transcript.isEmpty() && !firstResult.isPartial()) {
                                //System.out.println("Number of items are:::: " + firstResult.alternatives().get(0).items().size());
                                String speaker = " Speaker " + firstResult.alternatives().get(0).items().get(0).speaker() + ": ";
                                String pretranscript = "";
                                String posttranscript = "";
                                int i = 0;
                                //System.out.println("ITEMS ARE:   " + firstResult.alternatives().get(0).items());
                                while (i < firstResult.alternatives().get(0).items().size()) {
                                  //pretranscript +=  firstResult.alternatives().get(0).items().get(i).content() + " ";
                                  if (i > 0) {
                                    speaker = " Speaker " + firstResult.alternatives().get(0).items().get(i-1).speaker() + ": ";
                                  }
                                  //System.out.println(i);
                                  i++;
                                }
                                pretranscript = firstResult.alternatives().get(0).transcript();
                                posttranscript = speaker + pretranscript + "\n";
                                //System.out.println("Pre and post transcript after while loop is: PRE: " + pretranscript + "******** POST: " + posttranscript);

                                finalTranscript += posttranscript;
                                posttranscript = "";
                            }
                        }

                    }
                }).build();
    }


}
