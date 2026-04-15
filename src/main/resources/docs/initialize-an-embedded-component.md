# Initialize an embeddable component

We share our components as standalone javascript libraries from our CDN

| Component Name             | Description                                                  | Exposed var        | Prod Url                                                                                                                       | Dev Url                                                                                                                                 |
| :------------------------- | :----------------------------------------------------------- | :----------------- | :----------------------------------------------------------------------------------------------------------------------------- | :-------------------------------------------------------------------------------------------------------------------------------------- |
| **Own Lending experience** | Allows you to offer loans based on transactional data        | **R2Ole**          | [https://assets.r2capital.co/ole/r2-ole.js](https://assets.r2capital.co/ole/r2-ole.js)                                         | [https://assets.r2capital.co/ole/r2-ole\_dev.js](https://assets.r2capital.co/ole/r2-ole_dev.js)                                         |
| **Payments Plus**          | Allows you to leverage payments to your merchants through R2 | **R2PaymentsPlus** | [https://assets.r2capital.co/payments-plus/r2-payments-plus.js](https://assets.r2capital.co/payments-plus/r2-payments-plus.js) | [https://assets.r2capital.co/payments-plus/r2-payments-plus\_dev.js](https://assets.r2capital.co/payments-plus/r2-payments-plus_dev.js) |

# Config options

| attribute | Type              | default value | description                                                                                                            |
| :-------- | :---------------- | :------------ | :--------------------------------------------------------------------------------------------------------------------- |
| auth      | string (required) | null          | The [generated JWT Token](https://r2-api-docs.readme.io/docs/jwt-token-generation) that uniquely identifies a Merchant |
| mode      | string            | 'light'       | It will initialize the component using a *light* or a *dark* color palette                                             |

# Code Examples

For the following examples let's assume you want to embed the Own Lending Experience in your platform

## React

1. Load our library asyncrounusly, and make sure it's loaded before initializing our component, we recommend using [useScript](https://usehooks-ts.com/react-hook/use-script)
2. Iniitialize the component passing an existing DOM element as the anchor, and the config options
3. You should manage the loading and error state from your application

```javascript r2ComponentLoader.js
import * as React from 'react';
import useScript from './useScript';

// if using typescrit
declare const R2Ole: any;
// make sure to use the right url according to your environment
const r2ComponentUrl =  'https://assets.r2capital.co/ole-v2/r2-ole.js';

export default function App() {
  // Fetch Component library
  const componentStatus = useScript(r2ComponentUrl);
  const componentContainer = React.useRef(null);
  const [componentLoading, setComponentLoading] = React.useState(true);
  const [componentReady, setComponentReady] = React.useState(false);
  const [componentError, setComponetError] )= React.useState(false);

  React.useEffect(() => {
    // Wait for the library to be loaded
    if (componentStatus === 'ready') {

      const r2Component = new R2Ole(r2OleContainer.current, {
        auth: 'A_VALID_AUTH_TOKEN',
        mode: 'light', // or 'dark' if applicable
      });
      
      // our components initialization returns a promise as the Ready attribute
      r2Component.Ready()
      	.then(() => {
        	setComponentReady(true);
      	})
      	.catch(() => {
        	setComponentError(true);
      	})
      	.finally(()=> {
      		setComponentLoading(false);
      	})
    }

  }, [componentStatus]);
  
  
  return (
    <div>
      <h1>R2 Own Lending Experience with React</h1>
   	 {componentLoading && <div>Loading component...</div>}
    {componentError && <div>Something went wrong</div>}
      <div ref={r2OleContainer} />
  		
    </div>
  );
}
```

# Angular

Add .js from CDN in your angular.json

```json angular.json
"projects": {
    "angular-tour-of-heroes": {
        ...
        "architect": {
            ...
            "build": {
                ...
                "options": {
                    ...
                    "scripts": [
                        {"input": "<https://assets.r2capital.co/ole/r2-ole.js>"}
                    ]
                },
            },
        }
    }
}
```

Create target element in your html component.

```html r2.component.html
<div id="r2-component-root"></div>
<div id="r2-component-loading" *ngIf="showLoading">Loading R2 component...</div>
<div id="r2-component-root" *ngIf="showError">An error ocurred</div>
```

Create a Component wrapper

```typescript r2.component.ts
import {Component} from '@angular/core';
declare var R2Ole: any;

@Component({
  selector: 'r2-root',
  templateUrl: './r2.component.html',
  styleUrls: ['./r2.component.css']
})
export class AppComponent {
  title = 'angular-r2-integration';
  showLoading = true;
  showError = false;

  ngOnChanges() {
    this.initOle();
  }

  ngAfterViewInit() {
    this.initOle();
  }

  private initOle = () => {
    if (typeof(R2Ole) === 'undefined') {
      this.showError = true;
      this.showLoading = false;
      return;
    }
    var ole = new R2Ole(document.getElementById('r2-component-root'), {
      auth: 'A_VALID_AUTH_TOKEN'
    });
    ole.Ready.catch(() => {
      this.showError = true;
    })
    .finally(() => {
      this.showLoading = false
    })
  }
}
```

# AngularJS

Here's a simple way to embede our component via directives

```javascript
'use strict';

/**
 * @ngdoc function
 * @name embeddedAngularjsApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the embeddedAngularjsApp
 */
angular.module('embeddedAngularjsApp')
  .directive('r2Ole', function () {
    return {
      restrict: 'E',
      template: '<div id="ole-container" />',
      link: function (scope, el) {
        var Ole = new window.R2Ole(el.get(0), {
          auth: 'A_VALID_AUTH_TOKEN'
        });
      }
    };
  })
  .controller('MainCtrl', function () {});
```

# Events and event handling

Our components may trigger events directly to the curren Document, which you can catch and do custom actions based on those.

<Table align={["left","left","left"]}>
  <thead>
    <tr>
      <th>
        Event
      </th>

      <th>
        Data
      </th>

      <th>
        Description
      </th>
    </tr>
  </thead>

  <tbody>
    <tr>
      <td>
        TCS\_ACCEPTED:
        "R2|TCs\_ACCEPTED"
      </td>

      <td>
        `{"id": "EXTERNAL_MID", business_type: "Natural\|Juridico"}`
      </td>

      <td>
        Triggered when a Merchant accepts charing their PII data. It should be used to send PII data to R2 via webhooks
      </td>
    </tr>

    <tr>
      <td>
        LOAN\_REQUEST: "R2|LOAN\_REQUEST"
      </td>

      <td>
        `{"in_progress": bool}`
      </td>

      <td>
        Triggered when a Merchant enters (in\_progress: true) or leaves (in\_progress: false) the loan application form.
      </td>
    </tr>

    <tr>
      <td>
        LOAN\_REQUEST\_COMPLETED:\
        "R2|LOAN\_REQUEST\_COMPLETED"
      </td>

      <td>
        \{`"id": "EXTERNAL_MID"}`
      </td>

      <td>
        Triggered once, after the Merchant fullfill all the loan application steps, and an OK response is recieved
      </td>
    </tr>
  </tbody>
</Table>

For better use all events will be exposed from the component class

```javascript
const myEventHandler = (data) => {
	console.log(data);
  // your actual handler logic
}

document.addEventListener(R2Ole.events.TCS_ACCEPTED, myEventHandler)


```