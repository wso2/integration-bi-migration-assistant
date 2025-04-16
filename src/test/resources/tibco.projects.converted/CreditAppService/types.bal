public type HTTPRequestConfig record {|
    string Method;
    string RequestURI;
    json PostData = "";
    map<string> Headers = {};
    map<string> parameters = {};
|};
