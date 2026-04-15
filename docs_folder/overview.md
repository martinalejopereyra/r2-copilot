# Overview

# What is an Embedded Experience?

In order to make our Partner's life easier we provide embeddable components for our products to work seamlessly inside your platform. Here, you'll find the documentation needed to integrate our products.

# What should you do?

1. Request API access by emailing to [partners@r2.co](mailto:partners@r2.co)
2. We'll provide you 2 sets of credentials (keyId, JWTSecret) for **dev** and **prod** environments (**make sure to store them safely**)
3. You'll use this credentials to [generate JWT Tokens](https://r2-api-docs.readme.io/docs/jwt-token-generation) for user's authorization/authentication
4. From your platform include our script an [initialize our component](https://r2-api-docs.readme.io/docs/initialize-an-embeddable-component), passing the genarated JWT Token
5. Profit

Here's a sequence diagram for better understanding:

<Image align="center" src="https://files.readme.io/636794e-APIs_Diagram_1.png" />