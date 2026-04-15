Add a new callback.

# Add a new callback.

Important: It only allows callbacks from one unique financing each time.

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
    "/callbacks/": {
      "post": {
        "description": "Important: It only allows callbacks from one unique financing each time.",
        "requestBody": {
          "$ref": "#/components/requestBodies/callbacks.CallbackRequestArray"
        },
        "responses": {
          "201": {
            "description": "Created",
            "content": {
              "application/json": {
                "schema": {
                  "properties": {
                    "error": {
                      "type": "string"
                    },
                    "items": {
                      "items": {
                        "$ref": "#/components/schemas/callbacks.Callback"
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
        "summary": "Add a new callback.",
        "tags": [
          "callbacks"
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
    "requestBodies": {
      "callbacks.CallbackRequestArray": {
        "content": {
          "application/json": {
            "schema": {
              "items": {
                "$ref": "#/components/schemas/callbacks.CallbackRequest"
              },
              "type": "array"
            }
          }
        },
        "description": "callback data",
        "required": true
      }
    },
    "securitySchemes": {
      "JWT": {
        "in": "header",
        "name": "Authorization",
        "type": "apiKey"
      }
    },
    "schemas": {
      "callbacks.Callback": {
        "properties": {
          "created_at": {
            "type": "string"
          },
          "id": {
            "type": "string"
          },
          "updated_at": {
            "type": "string"
          },
          "url": {
            "type": "string"
          }
        },
        "type": "object"
      },
      "callbacks.CallbackRequest": {
        "properties": {
          "url": {
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