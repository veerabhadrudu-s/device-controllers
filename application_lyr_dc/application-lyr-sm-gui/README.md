# This service manager UI is common for all application layer protocol DC's.
# In order to differentiate and deploy individual service manager UI for each DC,
# We use angular cli command $ ng build--prod--base - href=/<dc name>/
# This base ref is accessible through BaseHrefProviderService.getBaseHref().
# BaseHrefProviderService service injected all over the DC to find out type of DC servcie manager.
# Example: $ ng build--prod--base - href=/kafka/, will gives dc name as kafka.
# Using this name we form remaning Rest Url's dynamically.
# --base-href is not provided we assume default name as mqtt

# Practice

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 1.6.5.

## Development server

Run `ng serve` for a dev server. Navigate to `http://localhost:4200/`. The app will automatically reload if you change any of the source files.

## Code scaffolding

Run `ng generate component component-name` to generate a new component. You can also use `ng generate directive|pipe|service|class|guard|interface|enum|module`.

## Build

Run `ng build` to build the project. The build artifacts will be stored in the `dist/` directory. Use the `-prod` flag for a production build.

## Running unit tests

Run `ng test` to execute the unit tests via [Karma](https://karma-runner.github.io).

## Running end-to-end tests

Run `ng e2e` to execute the end-to-end tests via [Protractor](http://www.protractortest.org/).

## Further help

To get more help on the Angular CLI use `ng help` or go check out the [Angular CLI README](https://github.com/angular/angular-cli/blob/master/README.md).
