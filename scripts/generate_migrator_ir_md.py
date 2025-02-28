import os
import subprocess

# Get the root directory of the project
root_dir = os.path.abspath(os.path.join(os.path.dirname(__file__), '..'))

# Define the relative paths from the root directory
relative_samples_dir = 'src/test/resources/blocks/mule3'
relative_output_md_path = 'src/main/java/ballerina/mule-to-ballerina-ir.md'

# Construct the absolute paths
samples_dir = os.path.join(root_dir, relative_samples_dir)
output_md_path = os.path.join(root_dir, relative_output_md_path)

# List all files in the directory and its subdirectories
all_files = []
for root, dirs, files in os.walk(samples_dir):
    for file in files:
        all_files.append(os.path.join(root, file))

# Sort all files to ensure consistent order
all_files.sort()

# Filter XML and BAL files
xml_files = [f for f in all_files if f.endswith('.xml')]
bal_files = [f for f in all_files if f.endswith('.bal')]

# Group files by their immediate parent directory
grouped_files = {}
for file in xml_files + bal_files:
    parent_dir = os.path.basename(os.path.dirname(file))
    if parent_dir == 'unsupported-block' or parent_dir == 'transform-message':
        continue
    if parent_dir not in grouped_files:
        grouped_files[parent_dir] = {'xml': [], 'bal': []}
    if file.endswith('.xml'):
        grouped_files[parent_dir]['xml'].append(file)
    elif file.endswith('.bal'):
        grouped_files[parent_dir]['bal'].append(file)

# Sort files within each group to ensure consistent order
for files in grouped_files.values():
    files['xml'].sort()
    files['bal'].sort()

# Pair XML and BAL files based on their names within each group
paired_files = {}
for parent_dir, files in grouped_files.items():
    paired_files[parent_dir] = []
    for xml_file in files['xml']:
        base_name = os.path.splitext(os.path.basename(xml_file))[0]
        for bal_file in files['bal']:
            if base_name in os.path.basename(bal_file):
                paired_files[parent_dir].append((xml_file, bal_file))
                break

# Function to read file content
def read_file_content(file_path):
    with open(file_path, 'r') as file:
        return file.read()

# Sort the paired_files dictionary by parent_dir keys in alphabetical order
sorted_paired_files = dict(sorted(paired_files.items()))

# Generate Markdown content
ir_intro_heading = '## Introduction to Ballerina Intermediate Representation (IR)'
ir_intro_description = '''\nAt a high level, Mule XML configuration file is read, and an intermediate representation (
IR) of a Ballerina file is created from it. This IR is then used to generate the Ballerina file. The IR is a Java object model; however, for convenience, the IR is shown below as JSON.

The IR provides a high-level structure of a Ballerina file. It represents import declarations, functions, listener and service declarations, variables, and other constructs in an abstract manner. Representing Ballerina statements, expressions, and types at a granular level would make the IR complex, so we have decided to represent them as strings. However, for statements with high-level abstractions such as if-else statements and do statements, we have used specific representations.

e.g. 
- function call statement
```json
{ "stmt" : "log:printInfo(\\"xxx: logger invoked via http end point\\");" }
```
- return statement
```json
{ "stmt" : "return self._invokeEndPoint0_();" }
```
- if else statement

```json
{
  "ifCondition": { "expr": "${payload} == \\"hello\\"}" },
  "ifBody": [
    { "stmt": "string _payload0_ = \\"Hi there!\\";" },
    { "stmt": "log:printInfo(\\"xxx: true condition reached\\");" }
  ],
  "elseIfClauses": [],
  "elseBody": [
    { "stmt": "string _payload1_ = \\"Goodbye!\\";" },
    { "stmt": "log:printInfo(\\"xxx: true condition reached\\");"}
  ]
}
```
- do statement
```json
{
  "doBody": [
    { "stmt": "log:printInfo(\\"xxx: logger invoked via http end point\\");" },
    { "stmt": "log:printInfo(\\"xxx: end of main flow reached\\");" }
  ],
  "onFailClause": {
    "onFailBody": [
      { "stmt": "log:printInfo(\\"xxx: exception caught\\");" },
      { "stmt": "log:printInfo(\\"xxx: end of catch flow reached\\");"}
    ]
  }
}
```

- query expression
```json
{ "expr" : "from var char in languageName select char + char;" }
```

- union type
```json
{ "type" : "anydata|http:Response|http:StatusCodeResponse" }
```
'''

mule_to_bal_conversion_samples_heading ='## Mule to Ballerina Conversion Samples (component-wise)'
mule_to_bal_conversion_descriptor ='''\nHere are several Mule to Ballerina conversion samples, organized by Mule 
component-wise.
The input is a Mule XML configuration file, and the output is a Ballerina file. The intermediate representation (IR) of the output is also provided as JSON.
'''

# Generate Markdown content for samples
markdown_content = ir_intro_heading
markdown_content += ir_intro_description
markdown_content += mule_to_bal_conversion_samples_heading
markdown_content += mule_to_bal_conversion_descriptor

# Define TOC entries
toc = '''# Table of Contents

- [Introduction to Ballerina Intermediate Representation (IR)](#introduction-to-ballerina-intermediate-representation-ir)
- [Mule to Ballerina Conversion Samples (component-wise)](#mule-to-ballerina-conversion-samples-component-wise)
'''

for parent_dir, pairs in sorted_paired_files.items():
    markdown_content += f'## {parent_dir.replace("-", " ").title()}\n\n'
    toc += f'    - [{parent_dir.replace("-", " ").title()}](#{parent_dir.replace("-", "-").title()})\n'
    for xml_file, bal_file in pairs:
        input_content = read_file_content(xml_file)
        output_content = read_file_content(bal_file)
        example_title = os.path.splitext(os.path.basename(xml_file))[0].replace("_", " ").title()

        # Generate JSON using getIRAsJson()
        json_output = subprocess.check_output(['java', '-cp', 'build/classes/java/main:build/libs/mule-to-bi-migration-assistant-0.1.0-SNAPSHOT.jar',
                                               'ballerina.IRMarkdownContentGenerator',
                                               'getIRAsJson', xml_file]).decode('utf-8')

        markdown_content += f'- ### {example_title}\n\n'
        markdown_content += f'**Input ({os.path.basename(xml_file)}):**\n```xml\n{input_content}\n```\n'
        markdown_content += f'**Output ({os.path.basename(bal_file)}):**\n```ballerina\n{output_content}\n```\n'
        markdown_content += f'**Intermediate Representation (IR):**\n```json\n{json_output}\n```\n\n'


bal_ir_json_schema_heading ='## Ballerina Intermediate Representation(IR) JSON Schema'
toc += ('- [Ballerina Intermediate Representation(IR) JSON Schema]('
        '#ballerina-intermediate-representationir-json-schema)\n\n')

# TODO: dynamically generate json schema
json_schema ='''
```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "BallerinaModel",
  "type": "object",
  "properties": {
    "defaultPackage": {
      "type": "object",
      "properties": {
        "org": { "type": "string" },
        "name": { "type": "string" },
        "version": { "type": "string" }
      },
      "required": ["org", "name", "version"]
    },
    "modules": {
      "type": "array",
      "items": {
        "type": "object",
        "properties": {
          "name": { "type": "string" },
          "textDocuments": {
            "type": "array",
            "items": {
              "type": "object",
              "properties": {
                "documentName": { "type": "string" },
                "imports": {
                  "type": "array",
                  "items": {
                    "type": "object",
                    "properties": {
                      "orgName": { "type": "string" },
                      "moduleName": { "type": "string" },
                      "importPrefix": { "type": "string" }
                    },
                    "required": ["orgName", "moduleName"]
                  }
                },
                "moduleTypeDefs": {
                  "type": "array",
                  "items": {
                    "type": "object",
                    "properties": {
                      "name": { "type": "string" },
                      "type": { "type": "string" }
                    },
                    "required": ["name", "type"]
                  }
                },
                "moduleVars": {
                  "type": "array",
                  "items": {
                    "type": "object",
                    "properties": {
                      "name": { "type": "string" },
                      "type": { "type": "string" },
                      "expr": { "type": "string" }
                    },
                    "required": ["name", "type"]
                  }
                },
                "listeners": {
                  "type": "array",
                  "items": {
                    "type": "object",
                    "properties": {
                      "type": { "type": "string", "enum": ["HTTP"] },
                      "name": { "type": "string" },
                      "port": { "type": "string" },
                      "config": {
                        "type": "object",
                        "additionalProperties": { "type": "string" }
                      }
                    },
                    "required": ["type", "name", "port"]
                  }
                },
                "services": {
                  "type": "array",
                  "items": {
                    "type": "object",
                    "properties": {
                      "basePath": { "type": "string" },
                      "listenerRefs": {
                        "type": "array",
                        "items": { "type": "string" }
                      },
                      "resources": {
                        "type": "array",
                        "items": {
                          "type": "object",
                          "properties": {
                            "resourceMethodName": { "type": "string" },
                            "path": { "type": "string" },
                            "parameters": {
                              "type": "array",
                              "items": {
                                "type": "object",
                                "properties": {
                                  "name": { "type": "string" },
                                  "type": { "type": "string" },
                                  "defaultExpr": { "type": "string" }
                                },
                                "required": ["name", "type"]
                              }
                            },
                            "returnType": { "type": "string" },
                            "body": {
                              "type": "array",
                              "items": { "$ref": "#/definitions/Statement" }
                            }
                          },
                          "required": ["resourceMethodName", "path", "parameters", "body"]
                        }
                      },
                      "functions": {
                        "type": "array",
                        "items": {
                          "type": "object",
                          "properties": {
                            "visibilityQualifier": { "type": "string" },
                            "methodName": { "type": "string" },
                            "parameters": {
                              "type": "array",
                              "items": {
                                "type": "object",
                                "properties": {
                                  "name": { "type": "string" },
                                  "type": { "type": "string" },
                                  "defaultExpr": { "type": "string" }
                                },
                                "required": ["name", "type"]
                              }
                            },
                            "returnType": { "type": "string" },
                            "body": {
                              "type": "array",
                              "items": { "$ref": "#/definitions/Statement" }
                            }
                          },
                          "required": ["methodName", "parameters", "body"]
                        }
                      },
                      "pathParams": {
                        "type": "array",
                        "items": { "type": "string" }
                      },
                      "queryParams": {
                        "type": "array",
                        "items": { "type": "string" }
                      }
                    },
                    "required": ["basePath", "listenerRefs", "resources", "functions", "pathParams", "queryParams"]
                  }
                },
                "functions": {
                  "type": "array",
                  "items": {
                    "type": "object",
                    "properties": {
                      "visibilityQualifier": { "type": "string" },
                      "methodName": { "type": "string" },
                      "parameters": {
                        "type": "array",
                        "items": {
                          "type": "object",
                          "properties": {
                            "name": { "type": "string" },
                            "type": { "type": "string" },
                            "defaultExpr": { "type": "string" }
                          },
                          "required": ["name", "type"]
                        }
                      },
                      "returnType": { "type": "string" },
                      "body": {
                        "type": "array",
                        "items": { "$ref": "#/definitions/Statement" }
                      }
                    },
                    "required": ["methodName", "parameters", "body"]
                  }
                },
                "Comments": {
                  "type": "array",
                  "items": { "type": "string" }
                }
              },
              "required": ["documentName", "imports", "moduleTypeDefs", "moduleVars", "listeners", "services", "functions", "Comments"]
            }
          }
        },
        "required": ["name", "textDocuments"]
      }
    }
  },
  "definitions": {
    "Statement": {
      "type": "object",
      "oneOf": [
        { "type": "object", "properties": { "stmt": { "type": "string" } }, "required": ["stmt"] },
        {
          "type": "object",
          "properties": {
            "ifCondition": { "type": "string" },
            "ifBody": { "type": "array", "items": { "$ref": "#/definitions/Statement" } },
            "elseIfClauses": {
              "type": "array",
              "items": {
                "type": "object",
                "properties": {
                  "condition": { "type": "string" },
                  "elseIfBody": { "type": "array", "items": { "$ref": "#/definitions/Statement" } }
                },
                "required": ["condition", "elseIfBody"]
              }
            },
            "elseBody": { "type": "array", "items": { "$ref": "#/definitions/Statement" } }
          },
          "required": ["ifCondition", "ifBody", "elseIfClauses", "elseBody"]
        },
        {
          "type": "object",
          "properties": {
            "doBody": { "type": "array", "items": { "$ref": "#/definitions/Statement" } },
            "onFailClause": {
              "type": "object",
              "properties": {
                "onFailBody": { "type": "array", "items": { "$ref": "#/definitions/Statement" } }
              },
              "required": ["onFailBody"]
            }
          },
          "required": ["doBody", "onFailClause"]
        }
      ]
    }
  }
}
'''

markdown_content += bal_ir_json_schema_heading
markdown_content += json_schema

# Write the Markdown content to the output file
with open(output_md_path, 'w') as file:
    file.write('###### This markdown file is AUTO-GENERATED by the test suite. Do not modify manually. ######\n\n')
    file.write(toc)
    file.write(markdown_content)

print(f'Mule to ballerina IR samples have been processed and written to {output_md_path}')
