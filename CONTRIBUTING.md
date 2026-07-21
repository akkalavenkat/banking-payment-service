# Contributing to Banking Payment Service

Thank you for your interest in contributing! This document provides guidelines for submitting pull requests and reporting issues.

## Getting Started

1. **Fork the repository**
   ```bash
   git clone https://github.com/your-username/banking-payment-service.git
   cd banking-payment-service
   ```

2. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **Set up development environment**
   ```bash
   mvn clean install
   docker-compose up -d postgres redis
   ```

## Code Style

- Use Java naming conventions (camelCase for variables, PascalCase for classes)
- Follow Spring Boot best practices
- Keep methods focused and under 30 lines
- Write meaningful variable and method names
- Add JavaDoc comments for public methods

### Example
```java
/**
 * Process a payment between two accounts
 *
 * @param request the payment request containing account numbers and amount
 * @return the processed payment response
 * @throws RuntimeException if accounts not found or insufficient funds
 */
public PaymentResponse processPayment(PaymentRequest request) {
    // Implementation
}
```

## Testing

- Write unit tests for new features
- Maintain at least 80% code coverage
- Use Mockito for mocking dependencies
- Test both happy path and error cases

```bash
# Run tests
mvn test

# Generate coverage report
mvn clean test jacoco:report
```

## Commit Messages

Follow conventional commit format:

```
type(scope): subject

body

footer
```

Types: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`

Examples:
```
feat(payment): add payment validation

fix(account): resolve balance update issue

docs: update README with API examples
```

## Pull Request Process

1. **Update documentation** if adding new features
2. **Run tests** to ensure all pass
3. **Update CHANGELOG.md** with your changes
4. **Provide clear PR description** explaining:
   - What problem does this solve?
   - How does it solve it?
   - Any breaking changes?

## Pull Request Template

```markdown
## Description
Brief description of the changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Related Issues
Fixes #(issue number)

## How Has This Been Tested?
Describe the tests you ran

## Checklist
- [ ] Tests pass locally
- [ ] Code follows style guidelines
- [ ] Documentation updated
- [ ] No new warnings generated
```

## Reporting Issues

Use the issue template:

```markdown
## Description
Clear description of the issue

## Steps to Reproduce
1. Step 1
2. Step 2

## Expected Behavior
What should happen

## Actual Behavior
What actually happens

## Environment
- Java version
- OS
- Spring Boot version
```

## Code Review Guidelines

When reviewing code:
- Check for security vulnerabilities
- Verify tests are comprehensive
- Ensure documentation is clear
- Suggest improvements respectfully

## Development Workflow

```bash
# Create feature branch
git checkout -b feature/new-feature

# Make changes and commit
git commit -m "feat(module): add new feature"

# Push to fork
git push origin feature/new-feature

# Create pull request on GitHub
```

## Architecture Principles

1. **Layered Architecture**: Controller → Service → Repository
2. **Dependency Injection**: Use @Autowired and constructor injection
3. **Transactional Management**: Use @Transactional for DB operations
4. **Error Handling**: Use consistent exception handling
5. **Caching**: Use @Cacheable/@CacheEvict for optimization

## Common Development Tasks

### Adding a New API Endpoint

1. Create DTO in `dto/` package
2. Add Repository method in `repository/` package
3. Add Service method in `service/` package
4. Create Controller method in `controller/` package
5. Add tests in `src/test/`
6. Update README and API_EXAMPLES.md

### Adding a New Database Entity

1. Create Entity in `entity/` package with JPA annotations
2. Create Repository interface in `repository/` package
3. Create appropriate indexes for performance
4. Run migrations (if using Flyway)
5. Create tests for repository queries

### Modifying Security

1. Update SecurityConfig if changing auth rules
2. Update JwtTokenProvider for token changes
3. Test with Auth Controller examples
4. Document security changes in README

## Performance Guidelines

- Use database indexes for frequently queried columns
- Implement caching for read-heavy operations
- Use pagination for large result sets
- Avoid N+1 query problems with proper JOIN strategies
- Monitor query performance with actuator/metrics

## Security Considerations

- Never commit secrets or credentials
- Use environment variables for sensitive data
- Validate all user input
- Use prepared statements to prevent SQL injection
- Follow OWASP guidelines for secure coding

## Release Process

Releases follow semantic versioning (MAJOR.MINOR.PATCH):

```bash
# Tag release
git tag v1.0.0

# Push tag
git push origin v1.0.0

# Build and publish
mvn clean deploy -P release
```

## Questions?

- Open a GitHub discussion
- Check existing issues and documentation
- Review API_EXAMPLES.md for usage patterns

Thank you for contributing!

---

**Contributing Guide Information**
- **Created**: January 2024
- **Last Updated**: 2024-01-15
- **Repository**: banking-payment-service
- **Owner**: akkalavenkat
