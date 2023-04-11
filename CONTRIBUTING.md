# How to Contribute

First off, thank you for contributing! We're excited to collaborate with you! 🎉

The following is a set of guidelines for the many ways you can join our collective effort.

Before anything else, please take a moment to read our [Code of Conduct](CODE-OF-CONDUCT.md). We expect all participants, from full-timers to occasional tinkerers, to uphold it.

## Reporting Bugs, Asking Questions, and Suggesting Features

Have a suggestion or feedback? Please go to [Issues](https://github.com/gyund/chatpaths/issues) and [open a new issue](https://github.com/gyund/chatpaths/issues/new). Prefix the title with a category like _"Bug:"_, _"Question:"_, or _"Feature Request:"_. Screenshots help us resolve issues and answer questions faster, so thanks for including some if you can.

## Submitting Code Changes

We use [Spotless](https://github.com/diffplug/spotless) to maintain a consistent code style consistent. Please run the `spotlessCheck` gradle task to check for any issues with your code (which can be fixed with `spotlessApply`).

### Kotlin

All features should be written in Kotlin.


### Prefer Coroutines to RxJava

When deciding between implementing something using RxJava and Coroutines, please use coroutines.


### Continuous Integration

When opening a PR from a fork, some of the CI checks must be manually triggered by a member of the team. That means you don't need to worry if some of the CI checks are not running—we'll take care of it when we review the PR and, if there are any issues, we'll let you know.

### PR merge policy

* PRs require one reviewer to approve the PR before it can be merged to the base branch
* We keep the PR git history when merging (merge via "merge commit")
* The reviewer who approved the PR may merge it right after approval (without waiting for the PR author) if all checks are green.
