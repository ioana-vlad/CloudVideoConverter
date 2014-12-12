CloudVideoConverter
===================

Scalable online video converter (between mpg, avi, psp, iphone/ipad; save audio to mp3) using Amazon Web Services (EC2 container service, Auto-scaling, S3, SQS).
EC2 and client side.
Client side: 
- Creates the credentials needed to connect to AWS and implements a method that downloads a specific file from a S3-bucket;
- Creates the credentials needed to connect to AWS and implements methods that upload files to S3 and write messages to SQS;
- UI.
EC2 side:
- Creates the credentials needed to connect to AWS and implements methods that download and delete files from S3 and retrieve messages from SQS;
- Converts an existing video-file using specific parameters and creates a new file;
- Creates the credentials needed to connect to AWS and implements methods that upload files to S3 and write messages to SQS;
- Cycles endlessly retrieving messages from a SQS-queue and processes them.
