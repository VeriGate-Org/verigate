# Development Workflow Instructions

## Branch Creation and Setup

When starting work on a new Jira issue, use these commands:

1. Create and checkout a new branch:
```bash
# Format: JIRA-KEY-description-in-kebab-case
# Example: VERIGATE-997-Verifications-CG-Command-Gateway-Architecture-Design-incl-SPIKE-for-architectural-diagram-etc
git checkout -b JIRA-KEY-description
```

2. Link the branch to the Jira issue:
```bash
# The branch name should start with the Jira issue key
git commit --allow-empty -m "JIRA-KEY: Initial commit for feature branch"
```

## Development Process

1. Make your code changes
2. Commit regularly with meaningful messages:
```bash
git add .
git commit -m "feat(scope): description [JIRA-KEY]"
```

## Creating Pull Request

When ready to create a PR, ensure your branch is up to date:
```bash
git pull origin main
git push origin your-branch-name
```

### PR Template Structure
```markdown
## Description
[Provide a detailed description of the changes]

## Related Issue(s)
- JIRA-KEY: [Issue Title]

## Testing Done
- [ ] Unit tests added/updated
- [ ] Integration tests added/updated
- [ ] Manual testing performed
```

### Share PR in Slack
After creating the PR:
1. Copy the PR URL (e.g., https://github.com/verigate/REPO-NAME/pull/PR_NUMBER)
2. Send to #verigate channel with message:
```
Hello from cursor, nice little PR for you peeps to review. Beep boop beep boop, I'm a good bot [PR_URL]
```

## Common Commands

### Open PR in Browser
```bash
open https://github.com/verigate/REPO-NAME/pull/PR_NUMBER
```

### Open Jira Issue
```bash
open https://verigate.atlassian.net/browse/JIRA-KEY
```

## Example Workflow

1. Given Jira issue VERIGATE-123:
```bash
git checkout -b VERIGATE-123-implement-new-feature
git commit --allow-empty -m "VERIGATE-123: Initial commit for feature branch"
```

2. After development, create PR:
```markdown
## Description
Implemented new feature X that does Y and Z.

## Related Issue(s)
- VERIGATE-123: Implement New Feature

## Testing Done
- [x] Unit tests added for feature X
- [x] Integration tests updated
- [x] Manual testing of edge cases
```

## Branch Naming Convention

Format: `JIRA-KEY-description-in-kebab-case`

Examples:
- `VERIGATE-997-Verifications-CG-Command-Gateway-Architecture-Design`
- `VERIGATE-998-implement-bank-verification-service`
- `VERIGATE-999-add-integration-tests`

## Commit Message Convention

Format: `type(scope): description [JIRA-KEY]`

Types:
- feat: New feature
- fix: Bug fix
- docs: Documentation changes
- style: Code style changes
- refactor: Code refactoring
- test: Test changes
- chore: Build process or auxiliary tool changes

Example:
```bash
git commit -m "feat(verification): implement bank account validation [VERIGATE-123]"
```
