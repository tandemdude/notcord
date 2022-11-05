# Notcord CLI App

This directory contains the source code for the `../notcord` cli bash script.

The script is generated using the Ruby tool [bashly](https://bashly.dannyb.co/).

## Installation

In order to update the CLI app, you first need to install `bashly` using `gem` (requires you to have Ruby 2.7 or higher installed). 

See the [bashly installation instructions](https://bashly.dannyb.co/installation/) in order to install the required tools on your machine.

## Usage

To regenerate the CLI app, you should run `bashly generate` in this directory.

Currently, the CLI app can only be run in the repository root, so you should then move the generated `./notcord` 
file into the root of the repository, overwriting any existing file.
