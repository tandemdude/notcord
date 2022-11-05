# Notcord

Notcord is an open-source discord and revolt like application pet project written using Java (spring-boot) for the
backend services, and using next.js for the frontend.

## Repository Info

This is the monorepo containing all key components that make up notcord.

### Project Structure

| **Directory** | **Description**                                                |
|---------------|----------------------------------------------------------------|
| `/cli`        | Contains the source code used to generate the bash CLI utility |
| `/frontend`   | Contains all the code used for the web frontend                |
| `/services`   | Contains the code for the different backend services           |

### Setup Information

Each of the directories contains a `README` file which you should refer to for the specific requirements in order
to build or run that component of notcord.

## Running Locally

If you are developing notcord locally, you'll probably want to run some or multiple parts of the stack in order to test.

A CLI tool is provided in the root of this repository which is designed to assist you with this process.

### CLI Commands

To view available commands, you can simply run `./notcord`, which will show a list of available commands and their descriptions.

Note that **all** commands **must** be run from the root of this repository else they will not run.

If you want to run the frontend component, you should first ensure that the required dependencies are installed, and
then you should be able to run it locally using the command:

```shell
./notcord run frontend
```

Before running any backend service, you must first ensure that the `commons` module is installed into your local repository
so that maven can build the services correctly. This can be done using the command:

```shell
./notcord install backend
```

This command only needs to be run once unless you change any of the code for the `commons` module, at which point it will need to
be reinstalled.

# Issues

If you find any bugs, issues, or unexpected behaviour while using notcord, you should open an issue with details of the
problem and how to reproduce if possible. 

Please also open an issue for any new features you would like to see implemented.

# Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change/implement.

If you use this application and like it, feel free to sign up to GitHub and star the project, it is greatly 
appreciated and lets me know that I'm going in the right direction!
