function _dwMethod1_(xml payload) returns json {
    //TODO: UNSUPPORTED DATAWEAVE EXPRESSION 'map$+1' OF TYPE 'xml' FOUND. MANUAL CONVERSION REQUIRED.
}

function _dwMethod2_(json payload) returns json {
    //TODO: UNSUPPORTED DATAWEAVE EXPRESSION 'groupBy$.language' FOUND. MANUAL CONVERSION REQUIRED.
}

function sampleFlow() {
    json _dwOutput_ = _dwMethod0_(payload);
    _dwOutput_ = _dwMethod1_(payload);
    _dwOutput_ = _dwMethod2_(payload);
}

function _dwMethod0_(json payload) returns json {
    float conversionRate = 13.15;
    return [1, 2, 3, 4];
    // DATAWEAVE PARSING FAILED.
    // line 7:13 mismatched input 'map' expecting {<EOF>, NEWLINE}

}
