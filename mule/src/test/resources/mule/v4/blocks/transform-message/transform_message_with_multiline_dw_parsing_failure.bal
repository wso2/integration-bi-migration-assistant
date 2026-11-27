public type Vars record {|
    any _dwOutput_?;
|};

public type Context record {|
    anydata payload = ();
    Vars vars = {};
|};

public function mule6demoFlow(Context ctx) returns error? {

    // TODO: DATAWEAVE PARSING FAILED.
    // ------------------------------------------------------------------------
    // Error details: line 2:7 mismatched input '*' expecting {IDENTIFIER, '*'}
    // line 5:27 mismatched input 'with' expecting {<EOF>, NEWLINE}
    // ------------------------------------------------------------------------
    // %dw 2.0
    // import * from dw::util::Values
    // output application/json
    // ---
    // payload update "OrderLine" with (payload.OrderLine filter((orderLine) -> !orderLine.IsReturn))
    // ------------------------------------------------------------------------

    ctx.vars._dwOutput_ = _dwOutput_;
    ctx.payload = _dwOutput_;
}
