## Amazon Transcribe Speaker Identification example application for real-time streaming transcriptions

Conversational audio data that requires transcription often have multiple speakers, such as phone calls, doctor visits, and online meetings. In these use cases, it is important to accurately identify the speaker and tag them to the audio content delivered. For example, you might want to identify the speaker and questioners in a live lecture, if this is being audio streamed to a remote audience.

With the launch of speaker identification for streaming transcriptions, you can now use Amazon Transcribe and Amazon Transcribe Medical to identify the different speakers in real-time customer service calls, conference calls, live broadcasts, or clinical visits. Being able to distinguish “who spoke when” is critical to creating accurate transcriptions. With speaker identification, you can request Amazon Transcribe and Amazon Transcribe Medical to accurately identify up to five speakers in an audio stream.

This example application shows you how to use the AWS Java SDK to start a stream that enables you to stream your conversational audio from your microphone to Amazon Transcribe, and receive transcripts in real time with speaker identification. The solution is a Java application that can be used to transcribe streaming audio from multiple speakers in real time. The application will label each speaker in the transcription results, which can be exported.

Use the following steps to setup the demo solution on your local computer. You need access to an AWS account (https://aws.amazon.com/console/) to proceed.

### Prerequisites

#### Install JavaFX

1. Follow these instructions (https://openjfx.io/openjfx-docs/#install-javafx) to download and install JavaFX. 
2. Setup the environment variable for JavaFX as shown in the instructions (https://openjfx.io/openjfx-docs/#install-javafx) page above or by typing: export PATH_TO_FX='path/to/javafx-sdk-14/lib' Replace the path/to with the directory where you installed JavaFX.
3. Test your JavaFX installation as shown in this sample application (https://github.com/openjfx/samples/blob/master/HelloFX/CLI).

#### Install Maven

1. Download Apache Maven from this page (https://maven.apache.org/download.cgi).
2. Follow the steps provided in these installation instructions (https://maven.apache.org/install.html).

#### Install AWS CLI (Optional)

1. Follow these instructions (https://docs.aws.amazon.com/cli/latest/userguide/install-cliv2.html) to install the AWS Command Line Interface (AWS CLI). This is an optional step, but can be used for validating or troubleshooting the solution as required.

#### Setup AWS Access

1. Please follow these instructions (https://docs.aws.amazon.com/general/latest/gr/aws-sec-cred-types.html#access-keys-and-secret-access-keys) to setup your Access Key and Secret Access Key required for programmatic access to AWS.
2. Select an AWS region closest to your location from this regions page (https://docs.aws.amazon.com/general/latest/gr/transcribe.html#transcribe_region). Refer to the table under *Amazon Transcribe Streaming*.
3. Once you know the region and access keys, open a terminal window in your computer and assign them to environment variables for access within our solution: 
    1. export AWS_ACCESS_KEY_ID='<access-key>'
    2. export AWS_SECRET_ACCESS_KEY='<secret-access-key>'
    3. export AWS_REGION='<aws region>'

### Solution Setup Steps

1. Clone the solution’s github repository in your local computer using the command: git clone https://github.com/aws-samples/aws-transcribe-speaker-identification-java
2. Navigate to the main directory of the solution “aws-transcribe-streaming-example-java” by typing cd aws-transcribe-streaming-example-java.
3. Let’s compile the source code and build a package for running our solution
    1. Type and enter mvn compile. If the compile is successful, you should a BUILD SUCCESS message in green color. If there are errors in compilation, this might most likely be related to JavaFX path issues. Please fix the issues based on instructions in the *Install JavaFX* section
    2. Type and enter mvn clean package. You should see a BUILD SUCCESS message if everything went well. This command compiles the source files and creates a packaged jar file that we will use to run our solution. If you are repeating the build exercise, you do not need to execute mvn compile every time.
4. Run the solution by typing  java --module-path $PATH_TO_FX --add-modules javafx.controls -jar target/aws-transcribe-sample-application-1.0-SNAPSHOT-jar-with-dependencies.jar
5. You should see a Java UI window open up

### Run the Demo Solution

1. Click on the *Start Microphone Transcription* button in the Java UI application
2. Use your computer’s microphone to stream an audio of two or more people (not more than 5) conversing in British English or US English. Amazon Transcribe Streaming supports the following languages currently:
    1. 8 KHz and 16 KHz
        1. US English (en-US)
        2. US Spanish (es-US)
    2. 8 KHz only
        1. Australian English (en-AU) - API only
        2. British English (en-GB) - API only
        3. French (fr-FR) - API only
        4. Canadian French (fr-CA) - API only
3. You should see the Speaker designations and the corresponding transcript appearing in the *In-Progress Transcriptions* window as the conversation progresses. Once the transcript is finalized for the discussion, it should appear in the *Final Transcription *window. 
4. Use the *Save Full Transcript* button to store the transcript locally in your computer


## Security

See [CONTRIBUTING](CONTRIBUTING.md#security-issue-notifications) for more information.

## License

This library is licensed under the MIT-0 License. See the LICENSE file.

