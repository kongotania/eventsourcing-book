## Understanding Eventsourcing - The Book

This is the sample project for the Book "Understanding Eventsourcing"

The first book to combine Eventmodeling, Eventsourcing to plan and build Software Systems of any size and complexity.

The Eventmodel is here:

[Eventmodel in Miro](https://miro.com/app/board/uXjVKvTN_NQ=/)

If you want to quickly learn about Eventmodeling, here is the original article:

[The original Eventmodeling Article](https://eventmodeling.org/posts/what-is-event-modeling/)

By subscribing to the newsletter you´ll get access to the "little" Eventmodeling Handbook, which can serve as a quick reference in addition to the book.

[The Little Eventmodeling Book](https://newsletter.nebulit.de/)

### Book

The book is written in public and current progress can always be checked [here](https://eventmodelers.de/das-eventsourcing-buch)

The Github Repository including all source code can be found here:
[Github](https://github.com/dilgerma/eventsourcing-book)

### Sample Application

The sample application is written in Kotlin / Spring / Axon

[Kotlin](https://kotlinlang.org/)
[Spring](https://spring.io/projects/spring-framework)
[Axon](https://www.axoniq.io/products/axon-framework)

You need to have Docker installed.

[Docker](https://www.docker.com/)

Here are the simple steps to start the application in a development environment.

install IntelliJ IDEA
Install the most recent Java SDK (File -> Project -> SDK - mine is JDK 23.0.1)
In the terminal type 'mvn clean install', this will do a full maven install of all the dependencies
Ensure you have docker running (if you don't already have it installed on your machine, you need it for testcontainers to work. on Windows just install the docker desktop app).
Build the app
