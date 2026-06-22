public type Context record {|
    anydata payload = ();
|};

function _dwMethod1_(json payload) returns json {
    // TODO: UNSUPPORTED DATAWEAVE EXPRESSION 'groupBy$.language' FOUND. MANUAL CONVERSION REQUIRED.
}

public function sampleFlow(Context ctx) {

    // TODO: DATAWEAVE PARSING FAILED.
    // ------------------------------------------------------------------------
    // Error details: line 7:13 mismatched input 'map' expecting {<EOF>, NEWLINE}
    // ------------------------------------------------------------------------
    // %dw 1.0
    // %dw 1.0
    // %output application/json
    // %input payload application/json
    // %var conversionRate=13.15
    // ---
    // [1, 2, 3, 4] map ,
    // ------------------------------------------------------------------------

    json _dwOutput_ = _dwMethod0_(ctx.payload.toJson());
    _dwOutput_ = _dwMethod1_(ctx.payload.toJson());
    ctx.payload = _dwOutput_;
}

function _dwMethod0_(xml payload) returns json {
    // TODO: UNSUPPORTED DATAWEAVE EXPRESSION 'map$+1' OF TYPE 'xml' FOUND. MANUAL CONVERSION REQUIRED.
}
