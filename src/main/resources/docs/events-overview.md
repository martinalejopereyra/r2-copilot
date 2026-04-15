

# Overview

# Events Overview

You can use events and callbacks to receive real-time notifications about actions that happen in the R2 Platform, allowing you to build automation that is triggered by a specific event in your lending flow, such as A new financing has been created, or A financing has been just paid, and so on.

## Note

You must implement a duplication validation process. We are responsible for sending these messages, you are responsible for avoiding duplications.

# What are R2 events

R2 Events, or webhooks, are messages about an event that are sent via POST request from R2's servers to a user-defined callback URL. They allow R2 to automatically send real-time event data to your app when triggered by an event (like a financing just created). You can think of webhooks as push notifications for servers.

# How R2 events are sent

R2 Events are sent as **POST** HTTP requests from R2’s server to a Third Party Server.

<Image alt="Events and callbacks workflow" align="center" border={true} src="https://files.readme.io/ed62305-Screenshot_2023-11-08_at_12.50.53.png">
  Events and callbacks workflow
</Image>

# Event names

### Here is a list of webhook events that can be sent to your callback URL:

Each event is configured into R2 platform regarding the integration process established with the partner.

| Event type               | Description                                                                                                                          | Attached API Object |
| ------------------------ | ------------------------------------------------------------------------------------------------------------------------------------ | ------------------- |
| financing\_created       | A new financing has just been created on R2’s platform                                                                               | FinancingEvent      |
| financing\_paused        | Charges over this financing are not being made, this can be due to multiple reasons depending on the country and the regulatory laws | FinancingEvent      |
| financing\_resumed       | Charges over this financing are being collected normally                                                                             | FinancingEvent      |
| financing\_paid          | The financing has been paid                                                                                                          | FinancingEvent      |
| financing\_active        | If the financing was paid and we need to re-activate it. (Because of a revert or a refund applied)                                   | FinancingEvent      |
| financing\_canceled      | In certain specific occasions, we need to cancel the financing                                                                       | FinancingEvent      |
| direct\_payment\_created | A new direct payment was made and it was informed to R2's team                                                                       | CollectionEvent     |
| \*collection\_created    | A new collection was created on R2's platform                                                                                        | CollectionEvent     |
| \*collection\_rejected   | A collections was not created on R2's platform                                                                                       | CollectionEvent     |
| collection\_returned     | In case we need to refund a total or partial amount of a related collection.                                                         | CollectionEvent     |
| \*retention\_created     | This is used when our partner needs to total amount of collections created for an specific period.                                   | RetentionEvent      |

(\*) events not deployed into production.