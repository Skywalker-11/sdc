# sdc - a simple framework for distributed computing in java

## How it works
This framework provides server and client to implement distributed computing with little overhead. 

You can create tasks on the server and queue them. The clients can connect dynamically to the server and request one task at a time. If a task is completed the result is send to the server and can be handled there.

## Example
You can find a simple example under src/main/java/de/sdc/examples/simple

# Build
To build this project you need maven and have to run:
$ mvn install
This will generate a jar in the target-folder and deploy it to your local maven repository.
