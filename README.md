# Replication Package  
## Towards Secure and Reliable Kubernetes Configurations

This replication package accompanies the paper:

> **Towards Secure and Reliable Kubernetes Configurations: A Taxonomy of Misconfigurations and Automated Correction with Large Language Models**

submitted to **The 23rd International Conference on Mining Software Repositories (MSR 2026)**.

The package provides all datasets, tools, and scripts required to **replicate the empirical analyses, taxonomy construction, severity assessment, and LLM-based correction experiments** presented in the paper.

---

## 📦 Contents

This repository contains the following components:

### 📁 Datasets
- **Stack Overflow Dataset**  
  Curated Kubernetes-related posts (2015–2024) used to construct the misconfiguration taxonomy.
- **GitHub Kubernetes Manifests**  
  10,000 schema-validated Kubernetes YAML configuration files collected from public GitHub repositories.
- **Helm Charts Dataset**  
  Kubernetes configurations from incubator and stable Helm projects used to study misconfiguration evolution.

### 📊 Taxonomy & Mapping
- The complete **taxonomy of Kubernetes misconfigurations** (5 main categories, 21 subcategories).
- Mapping tables aligning Datree, Kube-Score, and Snyk rule identifiers with taxonomy categories.
- Severity normalization into **Low / Medium / High** levels.

### 🔍 Misconfiguration Detection
- Scripts to execute and aggregate results from:
  - **Datree**
  - **Kube-Score**
  - **Snyk**
- Deduplication and consolidation of overlapping detections across tools.

### 🤖 LLM-Based Correction
- Prompt templates corresponding to the three experimental settings:
  1. Configuration only  
  2. Configuration + misconfiguration type  
  3. Configuration + type + detailed description
- Evaluation scripts for:
  - GPT-3.5-Turbo  
  - Mistral-7B  
  - LLaMA-3.1-70B  
  - DeepSeek-R1  
  - Qwen-2.5-7B  

## 🛡️ Kubecurity: Schema-Guided Kubernetes Configuration Correction

Kubecurity is a schema-guided, rule-based correction framework designed to improve the reliability of Large Language Model (LLM)–generated Kubernetes configurations.

While LLMs are effective at reasoning about misconfigurations and proposing semantic fixes, their outputs may violate Kubernetes structural constraints, including missing required fields, incorrect nesting, invalid data types, or schema-incompatible values. Kubecurity addresses this limitation by enforcing **deterministic compliance with official Kubernetes JSON schemas**.

### Design Principles

Kubecurity is built on the following principles:

- **Schema Awareness**  
  Each Kubernetes object is validated against its corresponding official JSON schema.
- **Deterministic Correction**  
  Fixes are rule-based and reproducible, independent of probabilistic model behavior.
- **LLM Complementarity**  
  Kubecurity does not replace LLM reasoning but reinforces it with structural guarantees.
- **Minimal Intervention**  
  Only schema-violating elements are modified or reconstructed.

### Correction Workflow

Kubecurity operates in two complementary stages:

1. **Pre-LLM Validation**
   - Repairs deterministic structural issues (e.g., invalid field placement, missing mandatory attributes)
   - Normalizes indentation, ordering, and object hierarchy

2. **Post-LLM Validation**
   - Re-validates LLM-generated configurations
   - Corrects residual schema violations introduced during generation
   - Ensures full compliance before final evaluation

### Correction Capabilities

Kubecurity performs the following actions:

- Reconstructs configuration trees to ensure correct parent–child relationships
- Inserts missing required fields using schema defaults
- Resolves type mismatches (e.g., string vs integer)
- Removes obsolete or invalid sections
- Enforces canonical Kubernetes object structure

### Role in the Experimental Pipeline

In the MSR 2026 experiments, Kubecurity is applied after LLM-based correction prompts. A configuration is considered **successfully corrected** only if:

1. It passes Kubernetes JSON schema validation, and  
2. It is no longer flagged by Datree, Kube-Score, or Snyk.

The integration of Kubecurity significantly improves correction robustness, reducing parsing failures and eliminating structurally invalid outputs. When combined with LLM reasoning, the framework achieves correction accuracy exceeding **97%**, demonstrating the effectiveness of hybrid semantic–syntactic correction.

### Limitations

Kubecurity focuses exclusively on **schema-level correctness**. It does not:
- Infer semantic intent beyond schema definitions
- Optimize configurations for performance or best practices
- Replace security reasoning performed by detection tools or LLMs

Its role is strictly to guarantee structural validity and deterministic compliance.

---

## 🔁 Replication Workflow

1. **Dataset Preparation**
   - Collect Kubernetes YAML files
   - Validate schemas using `kubeval`

2. **Misconfiguration Detection**
   - Run Datree, Kube-Score, and Snyk
   - Normalize severity and remove duplicate detections

3. **Taxonomy & Severity Analysis**
   - Map detections to taxonomy categories
   - Aggregate results by Kubernetes object type and category

4. **LLM-Based Correction**
   - Apply prompt configurations (Experiments 1–3)
   - Validate corrected outputs using schemas and detection tools

5. **Schema-Guided Correction**
   - Apply Kubecurity to enforce deterministic schema compliance
   - Re-evaluate corrected configurations

---

## 📄 Citation

If you use this replication package, please cite:



## 📜 License

MIT License

Copyright (c) 2026

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND.

## 📌 Notes

To be added 


## ⚠️ Work in Progress
This replication package will be extended with:

Additional datasets

New Kubecurity Version

Fully automated experiment pipelines

