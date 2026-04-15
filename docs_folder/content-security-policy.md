

# Content Security Policy (CSP)

To ensure seamless integration of the Embedded Experience into your platform, it's essential to configure your Content Security Policy (CSP) correctly. This document provides step-by-step instructions on how to update your CSP to allow necessary resources from multiple domains, thereby resolving CORS (Cross-Origin Resource Sharing) issues.

## Understanding Content Security Policy (CSP)

Content Security Policy (CSP) is a security standard designed to prevent various types of attacks, such as Cross-Site Scripting (XSS) and data injection attacks. CSP achieves this by allowing you to specify the sources from which your web application can load resources like scripts, styles, images, and more.

In the Embedded Experience, certain third-party libraries require access to scripts hosted on multiple domains. If these domains are not explicitly allowed in your CSP, browsers will block the requests, leading to functionality issues.

## Required Domains

<Table align={["left","left"]}>
  <thead>
    <tr>
      <th>
        Domain
      </th>

      <th>
        Usage
      </th>
    </tr>
  </thead>

  <tbody>
    <tr>
      <td>
        `https://docucdn-a.akamaihd.net`
        `https://na4.docusign.net`
        `https://demo.docusign.net` (dev/staging environments)
      </td>

      <td>
        Allows users to read and sign their credit contract inside the embedded experience
      </td>
    </tr>

    <tr>
      <td>
        `https://*.amplitude.com`\
        `https://*.customer.io`
      </td>

      <td>
        Analytics tracking
      </td>
    </tr>

    <tr>
      <td>
        `https://*.r2.co`\
        `https://*.r2capital.co`
      </td>

      <td>
        R2's internal purposes
      </td>
    </tr>

    <tr>
      <td>
        `https://*.ingest.us.sentry.io`
      </td>

      <td>
        Errors and performance monotiring
      </td>
    </tr>
  </tbody>
</Table>