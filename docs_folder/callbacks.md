# Callbacks

In order to receive notifications from R2’s side to your platform, it’s necessary for you to configure a callback url, all the financing events associated with your account are notified to that URL.  You can set your account callback by using the [account API call](https://r2-api-docs.readme.io/reference/post_callbacks-1).

# Financing Callbacks

financing\_id = Unique

### FinancingEvent group

## Payload Examples

```javascript Financing Created
{
    "event": {
        "event_time": 1348177752,
        "event_type": "financing_created",
        "event_metadata": {}
    },
    "financing": {
        "financing_id": "fa5c8a0b0f492d768749333ad6fcc214c111e967",
        "disbursed_amount": 12200,
        "total_repayment_amount": 14606,
        "repayment_rate": 17.36,
        "currency": "COP",
        "created_at": 1570471067,
        "status": "ACTIVE",
        "due_date": 1570471067,
        "external_merchant_id":"5509723654647999613",
        "financing_kind": "REFILL", // Possible values are "REGULAR" and "REFILL"
        "fixed_fee": 177.90
    }
}
```

```json Financing Paused
{
    "event": {
        "event_time": 1348177752,
        "event_type": "financing_paused",
        "event_metadata": {
            "reason": "CAP reached"
        }
    },
    "financing": {
        "financing_id": "fa5c8a0b0f492d768749333ad6fcc214c111e967"
    }
}
```

```json Financing Resumed
{
    "event": {
        "event_time": 1348177752,
        "event_type": "financing_resumed",
        "event_metadata": {
            "reason": "New cycle started, usury rate won't be surpased"
        }
    },
    "financing": {
        "financing_id": "fa5c8a0b0f492d768749333ad6fcc214c111e967"
    }
}
```

```json Financing Paid
{
    "event": {
        "event_time": 1348177752,
        "event_type": "financing_paid",
        "event_metadata": {}
    },
    "financing": {
        "financing_id": "fa5c8a0b0f492d768749333ad6fcc214c111e967"
    }
}
```

```json Financing Active
{
    "event": {
        "event_time": 1348177752,
        "event_type": "financing_active",
        "event_metadata": {
            "reason": "Financing active because of a RETURNED collection"
        }
    },
    "financing": {
        "financing_id": "fa5c8a0b0f492d768749333ad6fcc214c111e967"
    }
}
```

```json Financing Canceled
{
    "event": {
        "event_time": 1348177752,
        "event_type": "financing_canceled",
        "event_metadata": {
            "reason": "The debtor is considered uncollectible"
        }
    },
    "financing": {
        "financing_id": "fa5c8a0b0f492d768749333ad6fcc214c111e967"
    }
}
```

***

# Collections Callbacks

collection\_id = Unique

partner\_collection\_id = must be unique (you send this value into the collections API call)

### CollectionEvent group

## Payload Examples

```json Collection Created
{
    "event": {
        "event_time": 1348177752,
        "event_type": "collection_created",
        "event_metadata": {}
    },
    "collection": {
        "collection_id": "7620e694-b74c-4577-96a1-d9f50e2fd850",
        "financing_id": "f770847b-47ee-4484-8880-6dbb43d6d65f",
        "partner_collection_id": "69f033f6-92c1-41e4-a849-05cfde2c108b",
        "repayment_amount": 22.58,
        "collected_at": 1570471067,
        "created_at": 1570471067,
        "remaining_balance": 11765.14
    }
}
```

```json Collection Rejected
{
    "event": {
        "event_time": 1348177752,
        "event_type": "collection_rejected",
        "event_metadata": {
            "reason": "Financing is PAID"
        }
    },
    "collection": {
        "financing_id": "f770847b-47ee-4484-8880-6dbb43d6d65f",
        "partner_collection_id": "69f033f6-92c1-41e4-a849-05cfde2c108b",
        "repayment_amount": 22.58
    }
}
```

```json Direct Payment Created
{
    "event": {
        "event_time": 1348177752,
        "event_type": "direct_payment_created",
        "event_metadata": {}
    },
    "collection": {
        "collection_id": "7620e694-b74c-4577-96a1-d9f50e2fd850",
        "financing_id": "f770847b-47ee-4484-8880-6dbb43d6d65f",
        "repayment_amount": 22.58,
        "collected_at": 1570471067,
        "created_at": 1570471067,
        "remaining_balance": 11765.14,
        "original_repayment_amount": 70.10,
        "collection_kind": "REFILL", // Possible values are "DIRECT" and "REFILL"
    }
}
```

```json Collection Returned
{
    "event": {
        "event_time": 1348177752,
        "event_type": "collection_returned",
        "event_metadata": {}
    },
    "collection": {
        "collection_id": "7620e694-b74c-4577-96a1-d9f50e2fd850",
        "financing_id": "f770847b-47ee-4484-8880-6dbb43d6d65f",
        "repayment_amount":- 22.58,
        "collected_at": 1570471067,
        "created_at": 1570471067,
        "remaining_balance": 11765.14,
        "related_collection_id": "7620e694-b74c-4577-96a1-d9f50e2fd843"
    }
}
```

***

# Handling Events Code Examples

```python Python
from flask import Flask, request, jsonify

app = Flask(__name__)

@app.route('/callbacks/r2', methods=['POST'])
def receive_post_request():
    try:
        # Check if the Authorization header is present and contains the correct API key
        api_key = request.headers.get('Authorization')
        if api_key != 'YOUR_API_KEY':
            return jsonify({'error': 'Unauthorized'}), 401

        # Check if the Content-Type header is set to JSON
        content_type = request.headers.get('Content-Type')
        if content_type != 'application/json':
            return jsonify({'error': 'Invalid Content-Type'}), 400

        # Get the JSON data from the request body
        data = request.get_json()
        
        # Check the "event_type" field
        event_type = data.get('event', {}).get('event_type')

        if event_type == "financing_created":
            # Process the data for the "financing_created" event
            # Access the data using data['event'] and data['financing']
            # You can add your processing logic here for this event type
            return 'R2 API Event Received', 200

        elif event_type == "other_event_type":
            # Process the data for another event type
            # You can add your logic here for different event types
            return 'R2 API Event Received', 200

        else:
            # Handle unknown or unsupported event types
            return jsonify({'error': 'Unsupported event_type'}), 400

    except Exception as e:
        return jsonify({'error': 'Internal Server Error'}), 500

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)
```