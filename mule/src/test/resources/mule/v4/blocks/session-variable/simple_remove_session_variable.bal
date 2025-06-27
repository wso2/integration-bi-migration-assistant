public type SessionVars record {|
    string greeting?;
    string 'from?;
|};

public type Context record {|
    anydata payload = ();
    SessionVars sessionVars = {};
|};

public function weatherServiceFlow(Context ctx) {
    ctx.sessionVars.greeting = "hello session";
    ctx.sessionVars.'from = "USA";
    ctx.sessionVars.greeting = ();
    ctx.sessionVars.'from = ();
}
