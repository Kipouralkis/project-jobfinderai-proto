

# The Agent

## The Flow
1. **Initiation:**  Frontend calls the backend `/chat` endpoint.
2. **Orchestration:** (`AgentChatService`):
   - Prepares the history list.
   - Calls the `llm.handleConversation(history)`
3. **Communication:** (`TextGenerationService`):
   - Attaches the System Prompt (identity)
   - Attaches the Tool Schemas (capabilities)
   - Sends a POST request to the LLM API
4. **Inference (Remore LLM):**
   - Analyzes the history
   - Decides next action
5. **Parsing**
   - Receives JSON
   - Converts it into an LlmResponse (containing text or a ToolCall).
6. **Action** (`ToolService`):
   -If a tool call exists, `AgentChatService` executes it here. 
   - `ToolService` talks to the Database (JPA).
7. **Observation:**
   - The database result is added to history as a tool message.
   - The loop repeats back to Step 2 to let the AI "see" the result.
8. **Completion:**
   - Once the AI is finished, `AgentChatService` bundles the final text and any raw data 
   (like Job lists) into a ChatResponse for the Frontend.


## ```AgentChatService```
implements a multi-turn reasoning loop. This service manages a stateful conversation where the AI
can "call back" to the server to perform actions before finalizing a response to the user

### The multi-turn loop:

- context construction: The service injects a ```system``` prompt (the Agent's personality) and the user's current message into the existing ```history```
- Inference: the ```TextGenerationService``` evaluates the history 
- Tool interception and Observation:
  - If the AI decides to call a tool, the loop is interrupted
  - The service executes the tool, gets the result and converts it to a JSON string.
  - This result is added back to the ```history``` as a tool role message, allowing the AI to observe the data it fetched.
-Final Synthesis: Once the AI has enough information, it stops calling tools and provides a natural language response. 


## ```ToolService```: The execution Layer
While the AgentChatService acts as the brain, the ToolService serves as the "hands" of the agent. 
It is responsible for translating the AI's abstract intent into concrete side-effects within the system's infrastructure.

- Action Dispatching: Uses a switch expression to map AI function names to specific Java methods
- Entity Resolution: : Bridges the gap between the AI’s string-based arguments and the database’s relational model.
- Feedback loop: Every tool execution returns a String. This is not shown to the user but is fed back to the AI as an "Observation," allowing the agent to confirm success or explain a failure.

## Data Contracts and Communication

- ```Message``` primary unit of data, handling three roles: User, Assistant and Tool
- ```ToolCall``` generated when the AI decides to act. Represents a "deferred execution" request.
- ```LLM response ``` internal DTO used to capture the full output of a single LLM inference.
- ```ToolResult``` standardized container for whatever the Java services return.


The High‑Level Components (simple version)
1. AgentChatService — the brain loop
   Runs the reasoning cycle:

send history to LLM

detect tool calls

execute tools

feed results back

repeat until the LLM stops calling tools

This is the “thinking” part.

2. TextGenerationService — the mouth & ears
   Handles communication with the LLM:

attaches system prompt

attaches tool schemas

sends messages

parses responses

extracts tool calls

This is the “talking to the model” part.

3. ToolService — the hands
   Executes whatever the LLM asks for:

semantic search

reranking

job apply

admin review

RAG lookup

This is the “doing things in the real world” part.

4. Data Contracts — the language everyone speaks
   These keep everything consistent:

Message → user / assistant / tool

ToolCall → “please run this function”

ToolResult → “here’s what happened”

LlmResponse → parsed output from the model

This is the “grammar” of the agent.

🔄 The Loop (the part that feels chaotic until you see it clearly)
Step 1 — User speaks
Frontend sends:
“Find me remote Python jobs.”

Step 2 — Agent sends history to LLM
LLM sees:

system prompt

previous messages

user message

tool definitions

Step 3 — LLM decides
It might say:

Call semantic_search with query="remote python jobs"

Step 4 — ToolService executes
Backend runs vector search → returns job list.

Step 5 — Agent feeds result back
Adds a tool message:

Code
tool: semantic_search
content: { jobs: [...] }
Step 6 — LLM continues reasoning
Now it sees the job list and decides:

maybe rerank

maybe apply

maybe summarize

or maybe answer directly

Step 7 — Final answer
LLM eventually stops calling tools and returns natural language.

Step 8 — Frontend renders
assistant text

job cards

optional tool logs

🎯 The Cleanest High‑Level Summary
Here’s the version you can put in your README:

The system implements a tool‑using conversational agent.  
The frontend sends a message, the backend forwards the conversation to the LLM, and the LLM decides whether to respond normally or call a backend tool (e.g., semantic search, job apply, admin review).
If a tool is called, the backend executes it and feeds the result back into the conversation so the LLM can continue reasoning.
This loop continues until the LLM produces a final natural‑language answer, which may include structured data such as job lists.
The architecture cleanly separates responsibilities:

AgentChatService orchestrates the reasoning loop

TextGenerationService communicates with the LLM

ToolService executes backend actions

ToolCall, ToolResult, and LlmResponse define the data contracts
This creates a robust, extensible agent capable of multi‑step reasoning and real‑world actions.