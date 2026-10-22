public function _dwMethod(Context ctx) returns json => {
    "correlationId": ctx.correlationId,
    "status": "ok"
};
