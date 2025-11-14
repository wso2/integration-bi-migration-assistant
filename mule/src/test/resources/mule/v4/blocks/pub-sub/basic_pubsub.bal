import ballerina/log;
import ballerinax/gcloud.pubsub;

configurable string projectId = ?;
configurable string credentialsPath = ?;
configurable string subscriptionName = ?;

public type Attributes record {|
    map<string> uriParams = {};
|};

public type Context record {|
    anydata payload = ();
    Attributes attributes;
|};

listener pubsub:Listener pubsubListener = check new (
    subscriptionName,
    projectId = projectId,
    credentials = {credentialsPath: credentialsPath}
);

// TODO: placeholder listener for PubSubConfig
service on pubsubListener {
    remote function onMessage(pubsub:Message message, pubsub:Caller caller) {
        Context ctx = {attributes: {}};
        log:printInfo("Pub-Sub message received");
    }
}
