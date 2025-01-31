package dw.parser;

public class DataWeaveScripts {

    public static final String SCRIPT1 = """
            %dw 2.0
            var myRead = read("<car><color>red</color></car>",
                            "application/xml")
            output application/json
            ---
            {
            mySelection : myRead.car
            }""";

    public static final String SCRIPT2 = """
                            %dw 1.0
                            %output application/json
                            ---
                            {
                            address1: payload.order.buyer.address,
                            address2: null,
                            city: payload.order.buyer.city,
                            country: payload.order
            }
            """;
    public static final String SCRIPT3 = """
            %dw 1.0
            %output application/xml
            ---
            {
              order: {
                type: "Book",
                title: payload.title,
                details: "By " ++ payload.author ++ " (" ++ payload.year ++ ")"
              }
            }
            """;
    public static final String SCRIPT_DW = """
            %dw 1.0
            ---
            a
            """;
    public static final String SCRIPT_OUTPUT = """
            %output application/xml
            ---
            a
            """;
    public static final String SCRIPT_INPUT = """
            %dw 1.0
            %input payload application/json
            ---
            payload
            """;
    public static final String SCRIPT_OUTPUT_INPUT = """
            %dw 1.0
            %output application/xml
            %input payload application/json
            ---
            payload
            """;
    public static final String SCRIPT_NAME_SPACE = """
            %dw 2.0
            %namespace mes http://www.mulesoft.com/anypoint/SOA/message/v1.0
            ---
            a
            """;
    public static final String SCRIPT_CONSTANT = """
               %var conversionRate=13.15
               ---
               a
            """;
    public static final String SCRIPT_FUNCTION_VAR = """
            %dw 1.0
            %output application/json
            %var toUser = (user) -> {firstName: user}
            ---
            a
            """;
    public static final String SCRIPT_FUNCTION = """
            %dw 1.0
            %output application/json
            %function toUser(user){firstName: user.name}
            ---
            {
             user: toUser(payload)
            }
            """;

    public static final String SCRIPT_SIMPLE_STRING = """
            %dw 2.0
            %output application/json
            ---
            "Hello World"
            """;
    public static final String SCRIPT_SIMPLE_BOOLEAN = """
            %dw 2.0
            %output application/json
            ---
            true
            """;
    public static final String SCRIPT_SIMPLE_NUMBER = """
            %dw 2.0
            %output application/json
            ---
            123
            """;
    public static final String SCRIPT_SIMPLE_NUMBER_DECIMAL = """
            %dw 2.0
            %output application/json
            ---
            123.321
            """;
    public static final String SCRIPT_SIMPLE_DATE = """
            %dw 2.0
            %output application/json
            ---
            |2021-01-01|
            """;
    public static final String SCRIPT_SIMPLE_REGEX = """
            %dw 2.0
            %output application/json
            ---
            /(\\d+)/
            """;
    public static final String SCRIPT_ARRAY = """
            %dw 2.0
            %output application/json
            ---
            [1,2,3]
            """;
    public static final String SCRIPT_OBJECT = """
            %dw 2.0
            %output application/json
            ---
            { a: 1, b: 2}
            """;

}
