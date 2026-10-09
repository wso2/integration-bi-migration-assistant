import ballerina/file;

configurable string fileInboundEndpointPath = "C:/projects/Test/inbound/input";
configurable string fileInboundEndpointMoveAfterProcessPath = "C:/projects/Test/inbound/done";

public listener file:Listener fileInboundEndpointListener = new (
    path = fileInboundEndpointPath,
    recursive = false
);

// Synapse VFS inbound endpoints process each discovered file exactly once. file:Listener only reports files created after it starts, so pre-existing files are instead handled by a one-time directory scan in this project's init() function. A file created in the brief window between that scan and this listener actually starting, or one still being written when detected, may be missed or read prematurely - file:Listener has no equivalent to Synapse's file-locking/stability checks.
service on fileInboundEndpointListener {
    remote function onCreate(file:FileEvent event) returns error? {
        return fileInboundEndpointProcessFile(event.name);
    }
}
