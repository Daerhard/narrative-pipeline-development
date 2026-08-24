#!/usr/bin/env node
import { Server } from "@modelcontextprotocol/sdk/server/index.js";
import { StdioServerTransport } from "@modelcontextprotocol/sdk/server/stdio.js";
import { ListResourcesRequestSchema, ReadResourceRequestSchema, ListPromptsRequestSchema, GetPromptRequestSchema } from "@modelcontextprotocol/sdk/types.js";
import { readFileSync } from "fs";
import { fileURLToPath } from "url";
import { dirname, join } from "path";

const __dirname = dirname(fileURLToPath(import.meta.url));

const resources = [
  {
    uri: "npd://style-guide",
    name: "NPD Style Guide",
    description: "The Narrative Pipeline Development style guide — philosophy, vocabulary, rules, and examples",
    mimeType: "text/markdown",
    file: "resources/style-guide.md",
  },
  {
    uri: "npd://extensions/chain",
    name: "ChainExtensions.kt",
    description: "Core pipeline extension functions: proceedIf, suspendProceedIf, orElseThrow",
    mimeType: "text/plain",
    file: "resources/ChainExtensions.kt",
  },
  {
    uri: "npd://extensions/log",
    name: "LogExtensions.kt",
    description: "Logging extensions that observe a value without breaking the chain: logInfo, logWarning, logError",
    mimeType: "text/plain",
    file: "resources/LogExtensions.kt",
  },
  {
    uri: "npd://examples/user-core",
    name: "UserCore.kt",
    description: "Example use case implementation using NPD — shows proceedIf chains in a real Spring Boot coroutine service",
    mimeType: "text/plain",
    file: "resources/examples/UserCore.kt",
  },
  {
    uri: "npd://examples/user-core-feature",
    name: "UserCore.feature",
    description: "BDD feature file for UserCore — each Gherkin scenario maps directly to a branch in the NPD pipeline",
    mimeType: "text/plain",
    file: "resources/examples/UserCore.feature",
  },
  {
    uri: "npd://examples/user-core-test",
    name: "UserCoreTest.kt",
    description: "Cucumber step definitions for UserCore.feature — shows how NPD behavioral branches translate to test structure",
    mimeType: "text/plain",
    file: "resources/examples/UserCoreTest.kt",
  },
];

const prompts = [
  {
    name: "apply-npd",
    description: "Apply Narrative Pipeline Development style to Kotlin code — makes business behavior immediately readable and traceable to BDD specs",
  },
];

const promptMessages = {
  "apply-npd": [
    {
      role: "user",
      content: {
        type: "text",
        text: `You write Kotlin using the Narrative Pipeline Development (NPD) style. Apply these rules at all times:

## Core idea
Business behavior must be readable as a sequence of named steps — no reconstruction required. Each line in a pipeline answers: "what is the system doing?"

## Pipeline vocabulary
Use only these extension functions (from ChainExtensions.kt and LogExtensions.kt):

| Function | Type | Role |
|---|---|---|
| \`proceedIf { condition }\` | \`T → T?\` | Guard: continue only if condition holds (blocking) |
| \`suspendProceedIf { condition }\` | \`T → T?\` | Guard: continue only if condition holds (coroutine) |
| \`orElseThrow { exception }\` | \`T? → T\` | Resolve absence with a domain exception |
| \`logInfo/Warning/Error { message }\` | \`T → T\` | Observe without breaking the chain |
| \`let { transform }\` | \`T → R\` | Transform to the next value |
| \`also { sideEffect }\` | \`T → T\` | Side effect without breaking the chain |

## Rules

**Meaningful function names** — functions must say what the system is doing, not how:
\`\`\`kotlin
// correct
validateUser(user)
existsById(user)
updateUser(user)

// wrong — implementation detail leaking into the behavior
repository.findById(user.id).map { mapper.map(it) }
\`\`\`

**Explicit lambda parameter names** — always name the parameter when it is used inside the lambda:
\`\`\`kotlin
// correct
user.suspendProceedIf { user -> validateUser(user) }
    .let { user -> userRepositoryPort.updateUser(user) }

// wrong
user.suspendProceedIf { validateUser(it) }           // unclear what flows in
user.suspendProceedIf { validateUser(user) }         // silently shadows outer variable
    .let { userRepositoryPort.updateUser(it) }       // it has no name
\`\`\`

When the lambda parameter is genuinely unused, omit it entirely:
\`\`\`kotlin
user.suspendProceedIf { featureFlags.isEnabled("new-flow") }
\`\`\`

**Behavioral traceability** — every branch in the pipeline must correspond to a Gherkin scenario. The vocabulary of the code and the feature file must match:
\`\`\`kotlin
// production code
user.suspendProceedIf { user -> validateUser(user) }
    .orElseThrow { Exception("User is not valid") }
    .suspendProceedIf { user -> userRepositoryPort.existsById(user) }
    .orElseThrow { NotFoundException("User does not exist") }
    .let { user -> userRepositoryPort.updateUser(user) }
\`\`\`
\`\`\`gherkin
Scenario: Update a valid existing user
Scenario: Reject updating a user that does not exist
Scenario: Reject updating a user with an invalid zip code
\`\`\`

**When not to use a pipeline** — if a chain makes behavior harder to read, use \`if\`/\`when\` instead. Behavioral clarity over pipeline syntax.

## Example (full use case)
See resources npd://examples/user-core, npd://examples/user-core-feature, and npd://examples/user-core-test for a complete end-to-end example across production code, feature file, and step definitions.

## Reference
Full style guide: npd://style-guide
Extension functions: npd://extensions/chain, npd://extensions/log`,
      },
    },
  ],
};

const server = new Server(
  { name: "narrative-pipeline-development", version: "0.1.0" },
  { capabilities: { resources: {}, prompts: {} } }
);

server.setRequestHandler(ListResourcesRequestSchema, async () => ({
  resources: resources.map(({ uri, name, description, mimeType }) => ({
    uri,
    name,
    description,
    mimeType,
  })),
}));

server.setRequestHandler(ReadResourceRequestSchema, async (request) => {
  const resource = resources.find((r) => r.uri === request.params.uri);
  if (!resource) {
    throw new Error(`Resource not found: ${request.params.uri}`);
  }
  const text = readFileSync(join(__dirname, resource.file), "utf-8");
  return {
    contents: [{ uri: resource.uri, mimeType: resource.mimeType, text }],
  };
});

server.setRequestHandler(ListPromptsRequestSchema, async () => ({
  prompts,
}));

server.setRequestHandler(GetPromptRequestSchema, async (request) => {
  const messages = promptMessages[request.params.name];
  if (!messages) {
    throw new Error(`Prompt not found: ${request.params.name}`);
  }
  return { messages };
});

const transport = new StdioServerTransport();
await server.connect(transport);
