Create scenarios for testing purposes

# Create scenarios for testing purposes

Based on the provided scenario and metadata, this endpoint can create or restore merchants,
and create offers, financings, and applications.


# OpenAPI definition

```json
{
  "openapi": "3.0.3",
  "info": {
    "title": "Sandbox scenarios setup",
    "version": "1.0.0",
    "description": "Endpoint to create scenarios for testing."
  },
  "servers": [
    {
      "url": "https://gateway-dev.r2capital.co/v2/",
      "description": "Development environment (DEV)"
    }
  ],
  "paths": {
    "/sandbox/scenarios": {
      "post": {
        "summary": "Create scenarios for testing purposes",
        "description": "Based on the provided scenario and metadata, this endpoint can create or restore merchants,\nand create offers, financings, and applications.\n",
        "parameters": [
          {
            "name": "Authorization",
            "in": "header",
            "required": true,
            "description": "JWT token, without merchant id.",
            "schema": {
              "type": "string"
            }
          }
        ],
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "schema": {
                "$ref": "#/components/schemas/RequestBody"
              }
            }
          }
        },
        "responses": {
          "200": {
            "description": "Seed offers or financing application response",
            "content": {
              "application/json": {
                "schema": {
                  "oneOf": [
                    {
                      "$ref": "#/components/schemas/SeedOffersResponse"
                    },
                    {
                      "$ref": "#/components/schemas/FinancingApplicationRequestBody"
                    }
                  ]
                },
                "examples": {
                  "seed_offers_response": {
                    "summary": "Example of a seed_offers scenario response",
                    "value": {
                      "items": [
                        {
                          "id": "12e56130-8e25-4ac4-97a4-c981dd5f366c",
                          "disbursement_amount": {
                            "currencyCode": "USD",
                            "amountE5": 25000000
                          },
                          "repayment_rate": 20.5,
                          "status": "AVAILABLE",
                          "expires_at": 1730851200000
                        }
                      ],
                      "error": null
                    }
                  },
                  "financing_application_response": {
                    "summary": "Example of a financing_active scenario response",
                    "value": {
                      "application_id": "123e4567-e89b-12d3-a456-426614174000",
                      "financing_id": "223e4567-e89b-12d3-a456-426614174000",
                      "features": {
                        "disbursement_amount": 1000,
                        "repayment_rate": 10,
                        "total_repayment_amount": 1100
                      }
                    }
                  }
                }
              }
            }
          },
          "400": {
            "description": "[API002] bad request",
            "content": {
              "application/json": {
                "schema": {
                  "$ref": "#/components/schemas/SeedOffersResponse"
                },
                "examples": {
                  "bad_request": {
                    "value": {
                      "items": null,
                      "error": "[API002] bad request"
                    }
                  }
                }
              }
            }
          },
          "404": {
            "description": "[API001] resource not found",
            "content": {
              "application/json": {
                "schema": {
                  "$ref": "#/components/schemas/SeedOffersResponse"
                },
                "examples": {
                  "not_found": {
                    "value": {
                      "items": null,
                      "error": "[API001] resource not found"
                    }
                  }
                }
              }
            }
          },
          "409": {
            "description": "[API004] conflict",
            "content": {
              "application/json": {
                "schema": {
                  "$ref": "#/components/schemas/SeedOffersResponse"
                },
                "examples": {
                  "conflict": {
                    "value": {
                      "items": null,
                      "error": "[API004] conflict"
                    }
                  }
                }
              }
            }
          },
          "422": {
            "description": "[API003] cannot process request",
            "content": {
              "application/json": {
                "schema": {
                  "$ref": "#/components/schemas/SeedOffersResponse"
                },
                "examples": {
                  "cannot_process": {
                    "value": {
                      "items": null,
                      "error": "[API003] cannot process request"
                    }
                  }
                }
              }
            }
          },
          "429": {
            "description": "[API005] rate limit exceeded",
            "content": {
              "application/json": {
                "schema": {
                  "$ref": "#/components/schemas/SeedOffersResponse"
                },
                "examples": {
                  "rate_limit": {
                    "value": {
                      "items": null,
                      "error": "[API005] rate limit exceeded"
                    }
                  }
                }
              }
            }
          },
          "500": {
            "description": "[API006] internal server error",
            "content": {
              "application/json": {
                "schema": {
                  "$ref": "#/components/schemas/SeedOffersResponse"
                },
                "examples": {
                  "internal_error": {
                    "value": {
                      "items": null,
                      "error": "[API006] internal server error"
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  },
  "components": {
    "schemas": {
      "RequestBody": {
        "type": "object",
        "required": [
          "external_id",
          "scenario"
        ],
        "properties": {
          "external_id": {
            "type": "string",
            "description": "Unique identifier of the merchant to be created.",
            "example": "1234567890"
          },
          "metadata": {
            "allOf": [
              {
                "$ref": "#/components/schemas/ScenarioMetadata"
              }
            ],
            "description": "Additional metadata for the scenario."
          },
          "scenario": {
            "allOf": [
              {
                "$ref": "#/components/schemas/SandboxScenario"
              }
            ],
            "description": "Name of the scenario to be created.",
            "example": "financing_active"
          }
        }
      },
      "SeedOffersResponse": {
        "type": "object",
        "properties": {
          "items": {
            "type": "array",
            "description": "List of created offers. May contain nulls if some items fail to create.",
            "items": {
              "type": "object",
              "nullable": true,
              "properties": {
                "id": {
                  "type": "string",
                  "format": "uuid",
                  "description": "Offer ID.",
                  "example": "12e56130-8e25-4ac4-97a4-c981dd5f366c"
                },
                "disbursement_amount": {
                  "$ref": "#/components/schemas/Money"
                },
                "repayment_rate": {
                  "type": "number",
                  "format": "float",
                  "description": "Repayment percentage for each transaction of the user.",
                  "example": 20.5
                },
                "status": {
                  "allOf": [
                    {
                      "$ref": "#/components/schemas/OfferStatus"
                    }
                  ],
                  "description": "Offer status (AVAILABLE by default in `seed_offers`)."
                },
                "expires_at": {
                  "type": "integer",
                  "format": "int64",
                  "description": "Unix timestamp in milliseconds for the offer expiration."
                }
              }
            }
          },
          "error": {
            "type": "string",
            "nullable": true,
            "description": "Error message, if any."
          }
        }
      },
      "ScenarioMetadata": {
        "type": "object",
        "properties": {
          "amount_range": {
            "allOf": [
              {
                "$ref": "#/components/schemas/AmountRange"
              }
            ],
            "description": "Range of amounts for the offers (used in `seed_offers`)."
          },
          "auto_approve": {
            "type": "boolean",
            "default": false,
            "description": "Auto-approve application (used in `seed_offers`). Only if `bypass_kyc` is true.",
            "example": true
          },
          "bypass_kyc": {
            "type": "boolean",
            "default": false,
            "description": "Skip KYC during the application (used in `seed_offers`).",
            "example": true
          },
          "default_pii": {
            "type": "boolean",
            "default": false,
            "description": "Use default PII for the application (used in `seed_offers`).",
            "example": true
          },
          "disbursement_amount": {
            "type": "number",
            "description": "Disbursement amount of the financing.",
            "example": 1000
          },
          "expires_in_days": {
            "type": "integer",
            "description": "Number of days until offers expire (used in `seed_offers`).",
            "example": 30
          },
          "number_of_offers": {
            "type": "integer",
            "description": "Number of offers to be created (used in `seed_offers`).",
            "example": 5
          },
          "paid_percentage": {
            "type": "number",
            "description": "Paid percentage of the financing.",
            "example": 30
          },
          "repayment_status": {
            "allOf": [
              {
                "$ref": "#/components/schemas/RepaymentStatus"
              }
            ],
            "description": "Repayment status for `loan_active` and `financing_active`. Optional.",
            "example": "REGULAR"
          },
          "term_days": {
            "type": "integer",
            "default": 90,
            "description": "Number of days until the financing is due (used in `seed_offers` and `financing_active`).",
            "example": 30
          },
          "total_repayment_amount": {
            "type": "number",
            "description": "Total repayment amount of the financing.",
            "example": 1000
          }
        }
      },
      "AmountRange": {
        "type": "object",
        "properties": {
          "min": {
            "type": "number",
            "description": "Minimum amount.",
            "example": 100
          },
          "max": {
            "type": "number",
            "description": "Maximum amount.",
            "example": 1000
          }
        }
      },
      "RepaymentStatus": {
        "type": "string",
        "enum": [
          "REGULAR",
          "SLOW_PAYMENT",
          "BEHIND_PAYMENT",
          "PAYMENT_RECOVERY"
        ]
      },
      "SandboxScenario": {
        "type": "string",
        "enum": [
          "loan_active",
          "financing_active",
          "seed_offers",
          "pending_application"
        ]
      },
      "Money": {
        "type": "object",
        "properties": {
          "currencyCode": {
            "type": "string",
            "description": "ISO 4217 currency code (e.g., USD, MXN).",
            "example": "USD"
          },
          "amountE5": {
            "type": "number",
            "description": "Amount in scale 1e5 (e.g., 100.00 → 10000000).",
            "example": 25000000
          }
        }
      },
      "OfferStatus": {
        "type": "string",
        "enum": [
          "AVAILABLE"
        ]
      },
      "FinancingApplicationRequestBody": {
        "type": "object",
        "properties": {
          "application_id": {
            "type": "string",
            "description": "Unique identifier of the application created.",
            "example": "123e4567-e89b-12d3-a456-426614174000"
          },
          "features": {
            "allOf": [
              {
                "$ref": "#/components/schemas/Features"
              }
            ],
            "description": "Features of the financing."
          },
          "financing_id": {
            "type": "string",
            "description": "Unique identifier of the financing created.",
            "example": "223e4567-e89b-12d3-a456-426614174000"
          }
        }
      },
      "Features": {
        "type": "object",
        "properties": {
          "disbursement_amount": {
            "type": "number",
            "description": "Amount disbursed for the financing.",
            "example": 1000
          },
          "repayment_rate": {
            "type": "number",
            "description": "Percentage of merchant's sales deducted for repayment.",
            "example": 10
          },
          "total_repayment_amount": {
            "type": "number",
            "description": "Total amount to be repaid.",
            "example": 1100
          }
        }
      }
    }
  }
}
```