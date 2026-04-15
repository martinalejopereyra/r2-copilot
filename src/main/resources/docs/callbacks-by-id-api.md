Show a callback by its id as filtering key.

# Show a callback by its id as filtering key.

# OpenAPI definition

```json
{
  "openapi": "3.0.0",
  "info": {
    "contact": {
      "email": "support@r2capital.co",
      "name": "R2 Support"
    },
    "description": "## Introduction\n<p>Through our REST APIs, you'll be able to run an end-to-end capital program for your merchants. Specifically, you will: </br></p>\n<ul>\n<li>Securely share data about your merchants and their transactions so that R2 can score them</li>\n<li>Issue optimal financing offers for your preapproved merchants</li>\n<li>Launch marketing touchpoints so that preapproved merchants learn about their financing offers</li>\n<li>Send or capture specific data about a merchant to run R2's KYC process</li>\n<li>Learn when a financing has been made along with its specific terms</li>\n<li>Provide sales and/or repayment data on financed merchants</li>\n<li>Retrieve updated balances</li>\n<li>Renew financings</li>\n</ul>\n<p>We have language bindings in Shell. You can view code snippets on the right-hand panel, and you can switch the snippets' programming language using the tabs above the code view.</br>\nTo access our APIs, you need an access token. Please sign-up and get a new access token by registering at our developer portal.</p>",
    "title": "R2 APIs",
    "version": "0.1"
  },
  "paths": {
    "/events/{id}": {
      "get": {
        "parameters": [
          {
            "description": "uuid formatted ID.",
            "in": "path",
            "name": "id",
            "required": true,
            "schema": {
              "type": "string"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "content": {
              "application/json": {
                "schema": {
                  "properties": {
                    "error": {
                      "type": "string"
                    },
                    "items": {
                      "items": {
                        "$ref": "#/components/schemas/events.EventWrite"
                      },
                      "type": "array"
                    }
                  },
                  "type": "object"
                }
              }
            }
          },
          "400": {
            "description": "Bad Request"
          },
          "404": {
            "description": "Not Found"
          },
          "500": {
            "description": "Internal Server Error"
          }
        },
        "security": [
          {
            "JWT": []
          }
        ],
        "summary": "Show a callback by its id as filtering key.",
        "tags": [
          "events"
        ]
      }
    }
  },
  "servers": [
    {
      "url": "https://gateway-dev.r2capital.co:443/v2"
    }
  ],
  "components": {
    "securitySchemes": {
      "JWT": {
        "in": "header",
        "name": "Authorization",
        "type": "apiKey"
      }
    },
    "schemas": {
      "events.EventWrite": {
        "properties": {
          "data": {
            "type": "object"
          },
          "id": {
            "type": "string"
          },
          "kind": {
            "type": "string"
          },
          "stream_id": {
            "type": "string"
          },
          "stream_kind": {
            "type": "string"
          },
          "timestamp": {
            "type": "string"
          },
          "transition": {
            "$ref": "#/components/schemas/events.Transition"
          },
          "version": {
            "default": 1,
            "type": "integer"
          }
        },
        "type": "object"
      },
      "events.Transition": {
        "properties": {
          "client": {
            "type": "string"
          },
          "from": {
            "type": "string"
          },
          "to": {
            "type": "string"
          },
          "user": {
            "description": "systemx",
            "type": "string"
          }
        },
        "type": "object"
      }
    }
  },
  "x-readme": {
    "explorer-enabled": true,
    "proxy-enabled": true
  }
}
```