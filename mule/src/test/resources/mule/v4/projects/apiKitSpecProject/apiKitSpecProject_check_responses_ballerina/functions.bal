public function jsonPayload(anydata payload) returns json|error {
    // A flow may set its payload as JSON text, such as the value of a set-payload
    return payload is string ? payload.fromJsonString() : payload.toJson();
}
