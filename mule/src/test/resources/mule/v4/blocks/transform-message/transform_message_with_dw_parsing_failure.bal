public type Vars record {|
    null _dwOutput_?;
|};

public type Context record {|
    anydata payload = ();
    Vars vars = {};
|};

public function mule6demoFlow(Context ctx) {

    // TODO: DATAWEAVE PARSING FAILED.
    // ------------------------------------------------------------------------
    // Error details: line 5:18 no viable alternative at input '(item)->{intentionallyadded'
    // ------------------------------------------------------------------------
    // %dw 2.0
    // output application/json
    // ---
    // payload map (item) -> {
    //     intentionally added this line to fail dw parsing
    // }
    // ------------------------------------------------------------------------

    ctx.vars._dwOutput_ = _dwOutput_;
    ctx.payload = _dwOutput_;
}
