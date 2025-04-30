package dw.parser;

public class DataWeaveScripts {

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
    public static final String SCRIPT_SIMPLE_STRING_SINGLE_QUOTED = """
            %dw 2.0
            %output application/json
            ---
            'Hello'
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
            {
                date: |2021-01-01|,
                time: |23:59:56|,
                timeZone: |-08:00|,
                dateTime: |2003-10-01T23:57:59-03:00|,
                localDateTime: |2003-10-01T23:57:59|
            }
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
    public static final String SCRIPT_INPUT_PAYLOAD = """
            %dw 2.0
            %output application/json
            %input payload application/json
            ---
            { a: payload}
            """;
    public static final String SCRIPT_BUILTIN_SIZEOF = """
            %dw 2.0
            %output application/json
            %input payload application/json
            ---
            sizeOf [1,2,3,4]
            """;
    public static final String SCRIPT_BUILTIN_SIZEOF_IN_OBJECT = """
            %dw 2.0
            %output application/json
            %input payload application/json
            ---
            {
                hail1: sizeOf [1,2,3,4]
            }
            """;
    public static final String SCRIPT_BUILTIN_MAP = """
            %dw 1.0
            %output application/json
            %input payload application/json
            ---
            [1, 2, 3] map $ + 1
            """;
    public static final String SCRIPT_MAP_WITH_PARAMS = """
            %dw 1.0
            %output application/json
            %input payload application/json
            %function addOne(x) x + 1
            ---
            ["john", "peter", "matt"] map ((firstName, position) -> position ++ ":" ++ upper firstName)
            """;
    public static final String SCRIPT_BUILTIN_UPPER = """
            %dw 1.0
            %output application/json
            %input payload application/json
            ---
            upper "apple"
            """;
    public static final String SCRIPT_BUILTIN_LOWER = """
            %dw 1.0
            %output application/json
            %input payload application/json
            ---
            lower "APPLE"
            """;

    public static final String SCRIPT_SINGLE_OBJECT = """
            %dw 2.0
            %output application/json
            ---
            a: 1
            """;
    public static final String SCRIPT_BUILTIN_FILTER = """
            %dw 1.0
            %output application/json
            %input payload application/json
            ---
            [1, 2, 3, 4, 5] filter $ > 2
            """;
    public static final String SCRIPT_MATH_OPERATOR = """
            %dw 1.0
            %output application/json
            ---
            {
              addition: 10 + 5,
              subtraction: 10 - 5,
              multiplication: 10 * 5,
              division: 10 / 5
            }
            """;
    public static final String SCRIPT_LOGICAL_OPERATOR = """
            %dw 1.0
            %output application/json
            ---
            {
              res1: true and false,
              res2: true or false
            }
            """;
    public static final String SCRIPT_COMPARISON_OPERATOR = """
            %dw 1.0
            %output application/json
            ---
            {
              res1: 10 > 5,
              res2: 10 < 5,
              res3: 10 >= 5,
              res4: 10 <= 5,
              res5: 10 == 5,
              res6: 10 != 5
            }
            """;
    public static final String SCRIPT_COMPLEX_OPERATORS = """
            %dw 1.0
            %output application/json
            ---
            payload.EVENT_TYPE == 'Hail' and payload.MAGNITUDE >= 1 and payload.distance_in_miles <= 5
            """;
    public static final String SCRIPT_COMPLEX_OPERATORS_IN_OBJECT = """
            %dw 1.0
            %output application/json
            ---
            {
            hail1: (payload.EVENT_TYPE == 'Hail' and payload.MAGNITUDE >= 1 and payload.distance_in_miles <= 5)
            }
            """;

    public static final String SCRIPT_SAMPLE = """
            %dw 1.0
            %output application/json
            ---
            payload.EVENT_TYPE == 'Hail' and payload.MAGNITUDE >= 1 and payload.distance_in_miles <= 5)) )
            }
         
            """;
    public static final String SCRIPT_COMPLEX_OPERATORS_IN_OBJECT_WITH_FUNCTIONS = """
            %dw 1.0
            %output application/json
            ---
            [payload.resultSet1 filter (item,index) -> (item.EVENT_TYPE == 'Hail' and item.MAGNITUDE >= 1 and
             item.distance_in_miles <= 5)]
            """;
    public static final String SCRIPT_COMPLEX_OPERATORS_IN_OBJECT_WITH_FUNCTIONS2 = """
            %dw 1.0
            %output application/json
            ---
            {
                hail1: sizeOf (payload.resultSet1 filter ((item,index) -> (item.EVENT_TYPE == 'Hail' and 
                item.MAGNITUDE >= 1 and item.distance_in_miles <= 5)) ),
                hail2: sizeOf (payload.resultSet1 filter ($.EVENT_TYPE == 'Hail' and  $.magnitude >= 1.5 and 
                $.distance_in_miles <= 5) map { count: $.weather_event_id }),
                thunder: sizeOf ( payload.resultSet1 filter ($.EVENT_TYPE == 'Thunderstorm Wind' and  
                $.magnitude >= 30 and $.distance_in_miles <= 1) map { count: $.weather_event_id })
            }
            """;
    public static final String SCRIPT_WHEN_OTHERWISE = """
            %dw 1.0
            %output application/json
            ---
            {
              currency: "USD"
            } when payload.country == "USA"
            otherwise
            {
                  currency: "EUR"
            }
            """;
    public static final String SCRIPT_WHEN_OTHERWISE_NESTED = """
            %dw 1.0
            %output application/json
            ---
            {
                currency: "USD"
            } when payload.country =="USA"
            otherwise
            {
                currency: "GBP"
            } when payload.country =="UK"
            otherwise
            {
                currency: "EUR"
            }
            """;
    public static final String SCRIPT_REPLACE_WITH = """
            %dw 1.0
            %output application/json
            ---
            "admin123" replace /(\\d+)/ with "ID"
            """;
    public static final String SCRIPT_CONCAT_STRING = """
            %dw 1.0
            %output application/json
            ---
            "Hello" ++ "World"
            """;
    public static final String SCRIPT_TYPE_COERCION = """
            %dw 1.0
            %output application/json
            ---
            10 as :string
            """;
    public static final String SCRIPT_TYPE_COERCION_WITH_FORMAT = """
            %dw 1.0
            %output application/json
            ---
            10 as :string {format: "##,#"}
            """;
    public static final String SCRIPT_TYPE_COERCION_WITH_CLASS = """
            %dw 1.0
            %output application/json
            ---
            10 as :object {class : "soa.sfabs.SOAResponseInfoType\\$ServiceInfo"}
            """;
    public static final String SCRIPT_TYPE_COERCION_STRING_WITH_DIFFERENT_FORMATS = """
            %dw 1.0
            %output application/json
            ---
            {
              a: 1 as :string {format: "##,#"},
              b: now as :string {format: "yyyy-MM-dd"},
              c: true as :string
            }
            """;
    public static final String SCRIPT_TYPE_COERCION_NUMBER_WITH_DATE_FORMATS = """
            %dw 1.0
            %output application/json
            ---
            {
              mydate1: |2005-06-02T15:10:16Z| as :number {unit: "seconds"},
              mydate2: |2005-06-02T15:10:16Z| as :number {unit: "milliseconds"}
            }
            """;
    public static final String SCRIPT_TYPE_COERCION_TO_DATE = """
            %dw 1.0
            %output application/json
            ---
            {
              a: 1436287232 as :datetime,
              b: "2015-10-07 16:40:32.000" as :localdatetime {format: "yyyy-MM-dd HH:mm:ss.SSS"}
            }
            """;
}
