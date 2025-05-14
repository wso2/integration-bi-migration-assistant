public type SessionVars record {|
    string bar?;
|};

public type Context record {|
    anydata payload = ();
    SessionVars sessionVars = {};
|};

public function weatherServiceFlow(Context ctx) {
    ctx.sessionVars.bar = "hello session";
    ctx.sessionVars.bar = ();
}
