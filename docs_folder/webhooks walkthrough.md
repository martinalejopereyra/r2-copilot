# Walkthrough

This walkthrough covers how to build with R2 Events and Callbacks (aka "webhooks"), which are payloads of event metadata automatically sent to your app when something happens in R2.

# Setting Up R2 Events

Enabling R2 Events (`webhooks`) requires two distinct steps:

1. Setting the callback url at the account or app level, which are both covered in the sections directly below.
    1. Please check this section [JWT Token Generation](https://r2-api-docs.readme.io/docs/jwt-token-generation)
2. Responding to the event with to verify it was received.

# Set R2 Callback Url

* You can change your account callback url with the R2 API by sending a `PUT` request to `/callbacks` and passing a `url`.

```asp Callback URL Update
curl -X PUT 'https://gateway-dev.r2capital.co:443/v2/callbacks' \
     -H 'Authorization: YOUR_R2_JWT_TOKEN' \
     -H 'Content-Type: application/json' \
     -d '{"url":"http://example.com/callbacks/r2"}'
```

* Alternatively, your account callback url can be changed from your API [settings]() page.

# Responding to Events

R2 sends events to a callback url and expects a specific response in order to verify that events are being sent to a live server. Once an event is sent, the callback url must return an HTTP `200` with a response body that contains the string `R2 API Event Received`. If no response is received, R2 considers that a failed callback and the event will be sent again later.

R2 will try up to 6 times to send the event message, according to the retry police described in [Failures and Retries](https://r2-api-docs.readme.io/docs/walkthrough#failures-and-retries).

Note: Every try will read the last `url` from R2 database.

# Testing your Callback Url

When setting the callback url for your app or account in the Partners platform web UI, you can trigger a test event by clicking the test button next to the field. This is a great way to verify that your webhook handler is responding to events successfully.

* Successful response:

```Text Success Response
"R2 API Event Received"
```

* Failed response (Could be a free text to describe the error)

```Text Failed Response
"Error on server"
```

# Callback Request Format

Once you've setup a callback url, R2 sends event data to that URL in the form of `POST` requests. This section contains information about how those requests are formatted so you can better understand how to interact with the event data.

## Content Type

By default, the `POST` requests are sent as json.

## Event Payload

Event payloads always include an `event` field, which contains basic information about the event that occurred (such as time and type). Event payloads may include a `financing`, `application`, `payment`, `collection` depending on what event took place. Here's an example of an event payload for a `financing` event:

```json Example Event Payload
{
    "event": {
        "event_time": "1348177752",
        "event_type": "financing_created",
        "event_metadata": {}
    },
    "financing": {
        "financing_id": "fa5c8a0b0f492d768749333ad6fcc214c111e967",
        "disbursed_amount": 1000,
        "total_repayment_amount": 1500,
        "repayment_rate": 0.14,
        "currency": "COP",
        "metadata": {},
        "created_at": 1570471067,
        "custom_fields": [],
        "requester_email_address": "me@dropboxsign.com"
    }
}
```

## Event Type

Every event payload contains an `event_type` parameter that provides the name of the specific event that occurred:

```json Example Event Type
{
    "event": {
        "event_time": "1348177752",
        "event_type": "financing_created",
        "event_metadata": {}
    },
    "financing": {
        ...
    }
}
```

```json Example Event Type
{
    "event": {
        "event_time": "1348177752",
        "event_type": "application_created",
        "event_metadata": {}
    },
    "application": {
        ...
    }
}
```

Generally speaking, checking the `event_type` is the best approach to filtering for specific events and automating your integration with the R2 API.

```javascript Event Type Checking Example
if (event.event_type === "financing_created") {
  // create financing in your platform
}
```

# Securing your Callback Handler

## IP Address Whitelisting

This is the full list of IP addresses that webhook events may come:

```shell bash
{
  "creationDate": "2024-01-22-14-11-43",
  "ips": {
    "us": [
      "54.151.119.16/32",
      "54.215.38.39/32"
    ]
  }
}
```

> 🚧 This list will be automatically updated if the IP addresses change. We recommend checking this list periodically to ensure your callback handler is secure.

> 📘 Currently we have just one IP address per environment. PROD 54.215.38.39, and DEV 54.151.119.16

## HTTP Headers

We provide a couple of headers on callback requests to help you identify them.

<Table align={["left","left","left"]}>
  <thead>
    <tr>
      <th>
        Name
      </th>

      <th>
        Description
      </th>

      <th>
        Value
      </th>
    </tr>
  </thead>

  <tbody>
    <tr>
      <td>
        User-Agent
      </td>

      <td>
        A token identifying us
      </td>

      <td>
        R2 API
      </td>
    </tr>

    <tr>
      <td>
        Content-Sha256
      </td>

      <td>
        A base64 encoded SHA256 signature of the request's JSON payload, generated using your jwtSecret.

        `echo -n $json \| openssl dgst -sha256 -hmac $jwtSecret`
      </td>

      <td>
        Example value:\\

        ```
        Y2Y2MzVhOTdiZDVhZ
        mVhNWRiYWJmMmRi
        ZGRhOGQzYWE3OGU
        1NWIxNDkzMzgzNzdj
        MWI5M2Y1OGEzYzEy
        NzZjMg=
        ```
      </td>
    </tr>
  </tbody>
</Table>

# Failures and Retries

If your callback url is not reachable or returns a non-successful response, we will retry POSTing the event up to **6 times**, with each retry interval being exponentially longer than the previous one. If the sixth retry fails, we will clear your callback url and you must enter a new one to receive future callback events.

Please note that our requests will timeout after **30 seconds**, so callbacks will fail if your server takes longer than that to respond. The retry pattern is described below, an alert email will be sent to you after POSTing has failed several times.

| Retry | Delay After Previous Attempt |
| :---- | :--------------------------- |
| 1st   | 5min                         |
| 2nd   | 15min                        |
| 3rd   | 45min                        |
| 4th   | 2h 15min                     |
| 5th   | 6h 45min                     |
| 6th   | 20h 15min                    |