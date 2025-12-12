# Snyk – Infrastructure as Code (IaC) Scanner

**Snyk** is a security tool used to detect vulnerabilities and misconfigurations in Infrastructure-as-Code (IaC) files, including Kubernetes YAML configuration files.

## 🔗 Official Link
- Website: [https://snyk.io](https://snyk.io)
- CLI Documentation: [https://docs.snyk.io/snyk-cli](https://docs.snyk.io/snyk-cli)

## 🧪 Usage in This Study

Snyk was used to automatically annotate Kubernetes configuration files by flagging security-related misconfigurations. It complements Datree and Kube Score by focusing on vulnerability detection, particularly in container settings and access control.

Since Snyk is a proprietary tool, its source code is not included in this replication package. However, its usage can be reproduced by following the instructions below.

## 🛠️ Installation

Snyk requires Node.js. It can be installed globally using the following command:

```bash
npm install -g snyk
```

## 🔐 Authentication

Before using Snyk, you need to authenticate with your Snyk account (a free account is sufficient):

```bash
snyk auth
```

This will open a browser window for login.

## 🚀 Example Usage

To analyze a Kubernetes YAML configuration file:

```bash
snyk iac test path/to/your/file.yaml
```

To export the results in JSON format:

```bash
snyk iac test path/to/your/file.yaml --json > snyk_output.json
```

## 📄 Notes

- All files must be syntactically valid YAML files before analysis.
- Snyk detects security issues such as `privileged: true`, missing `readOnlyRootFilesystem`, and improper RBAC policies.
- In this study, only files flagged as misconfigured by Snyk were retained for dataset labeling and model training.
