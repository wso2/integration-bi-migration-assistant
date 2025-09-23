public type Context record {|
    anydata payload = ();
|};

function _dwMethod1_(Context ctx) returns json {
    //TODO: UNSUPPORTED DATAWEAVE EXPRESSION 'map$+1' OF TYPE 'xml' FOUND. MANUAL CONVERSION REQUIRED.
}

function _dwMethod0_(Context ctx) returns json {
    float conversionRate = 13.15;
    return [1, 2, 3, 4];
    // TODO: DATAWEAVE PARSING FAILED. MANUAL CONVERSION REQUIRED.
    // ------------------------------------------------------------------------
    // line 7:13 mismatched input 'map' expecting {<EOF>, NEWLINE}
    // ------------------------------------------------------------------------
    // %dw 1.0
    // %dw 1.0
    // %output application/json
    // %input payload application/json
    // %var conversionRate=13.15
    // ---
    // [1, 2, 3, 4] map ,
    // ------------------------------------------------------------------------
}

public function sampleFlow(Context ctx) {
    json _dwOutput_ = _dwMethod0_(ctx.payload.toJson());
    _dwOutput_ = _dwMethod1_(ctx.payload.toJson());
    _dwOutput_ = _dwMethod2_(ctx.payload.toJson());
    ctx.payload = _dwOutput_;
}

function _dwMethod2_(Context ctx) returns json {
    //TODO: UNSUPPORTED DATAWEAVE EXPRESSION 'groupBy$.language' FOUND. MANUAL CONVERSION REQUIRED.
}
