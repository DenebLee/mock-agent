# Mock Agent

![GitHub last commit](https://img.shields.io/github/last-commit/DenebLee/MockAgent) ![GitHub repo size](https://img.shields.io/github/repo-size/DenebLee/MockAgent) ![GitHub repo file count](https://img.shields.io/github/directory-file-count/DenebLee/MockAgent)

## Table of Contents

- [Overview and Description](#overview-and-description)
  - [Project Goals](#project-goals)
- [Scenario](#scenario)
  - [Demand Scenario](#demand-scenario)
  - [Transfer Scenario](#transfer-scenario)
  - [Receive Scenario](#receive-scenario)
- [License](#license)


## Overview and Description

A project to develop a test agent so that it can respond to agent tasks

### Project Goals
- Create Test Codes
- Policy
  - TPS ( Transfer Per Second ), throttling, rate limit
    - SMS 80 TPS
    - LMS 50 TPS
    - MMS 10 TPS
  - Multi Connection
    - Multiple accounts
    - Multiple Database
  - Responding to message transfer bottlenecks in a multi-account agent architecture
  - Serialize to work with FunSMS real server as a follow-up, use INTERFACE unconditionally for de-serialization components

## Scenario
### Demand Scenario
- Send a message to a specific company
- Currently, DB exists, and if you create a DB, you will put data in that DB.
- Messages must be delivered unconditionally and results must fall regardless of the outcome
- Messages can be sent in bulk or in single cases
- When the result comes from G/W, it should be reflected in the DATA BASE
- When data is entered into the DATABASE, AGENT sends it to G/W

### Transfer Scenario
- User creates a transfer table when installing 'agent'**
  - Initial use database fixed and tested with 'postgresql'
  - Modify and implement to support other databases in the future
- Message 'insert' in the forwarding table
  - Insert to the table created by agent
  - Do you want to 'insert' directly to the DB or copy the values in the other table?
  - Implement logic to support both
  - Select the table in which 'SendThread' was created during the initial execution.
  - If there is no selected value, handle 'NullException' and transfer the responsibility so that the scheduler can 'select'.
  - Process shipment before scheduler selects if first selected data is present
  - Handling required to avoid duplicating selected data
- Attempt to select the table in which 'SendThread' was created during initial execution
  - If the value to be selected exists in the table, take it, put it in 'Queue', and start sending it
  - Pass if the value to be initially selected does not exist in the table
  - Handling "NullException" that pops up.
- Scheduler
  - Use Java basic library 'ScheduledExecutorService' without 'CoreQuartz'
  - Set 'initialDelay' and execution period to 5 seconds
  - If no data is selected, 'Exception' processing is required.
      ```java
      this.scheduledExecutorService = Executors.newScheduledThreadPool(1);
      scheduledExecutorService.scheduleAtFixedRate(this::selectData, 2000, 5000, TimeUnit.MICROSECONDS);
      ```

- Send to developed G/W in 'Json' format
  - G/W support format is JSON format
  - `SEND` , `SEND_ACK`, `REPORT`, `REPORT_ACK`, `AUTHENTICATION`, `AUTHENTICATION_ACK`
  - `Payload` → `type`, `messageUuid` , `Object`(`SEND` etc )
  - 'Socket.close' if 'Authentication' authentication fails
- Work later upon completion of transmission
  - Map initialization work with selected data collected
    - Perform initialization on successful transfer
    - Missing data check logic required in queue
    - If a transfer fails, continue with the transfer failed.
      - Use Custom Exception
- Update status so that selected and transferred columns are not imported in duplicate
- Using DB Cursors? → Acquire prior knowledge and identify usability
- Leverage Log to provide error identification for users

### Receive Scenario
- Received via 'ReceiveThread' upon completion of transmission
  - Incoming data type
      ```java
      public class Payload {
          private PayloadType type;
          private String messageUuid;
          private Object data;
      }
      ```
  - Type of data that goes into the object

    Authentication_ACK
      ```java
      public class AuthenticationAck {
          private long agent_id;
          private String result;
      }
      ```

    Send_ACK
      ```java
      public class SendAck {
          private MessageResult result; -> // SUCCESS or FAILED
      }
      ```

    Report
      ```java
      public class SendAck {
          private MessageResult result;
      }
      ```

    Error
      ```java
      public class ErrorPayload {
          private String reason;
      }
      // Need to be corrected so that the error can be delivered in the ack after deleting it later
      ```

- Validation
  - Check if it fits the Entity format that matches the table column
  - The value should not contain null.
  - Logic implementation for the reason is required in case of a failed transfer
    - Only SUCCESS or FAILED exists in the logic that we have planned.
    - If an error occurs in G/W, the error payload is sent separately, so there is currently no way for the agent to respond.
- Data 'insert' in 'ReceiveTable'
  - Load data to be inserted into 'Queue' one by one after validation in 'ReceiveThread'
  - Insert data into the 'Receive' table generated when the agent runs
  - Alert users through 'Log' upon successful insert
  - Requires complete test code for insert failure
    - Write test code corresponding to failure conditions under different conditions
    
  
## License

[NANO-IT](LICENSE) © JeongSeob LEE
