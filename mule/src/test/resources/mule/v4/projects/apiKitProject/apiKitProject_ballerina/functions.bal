import ballerina/log;

public function get\:\\orders\\\(id\)\:apikit\-config(Context ctx) {
    log:printInfo(string `Received order id: ${id.toString()}`);
}
