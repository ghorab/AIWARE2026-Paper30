# Replication Package – AIWARE 2026

## Kubernetes Misconfigurations in the Wild:
### Taxonomy, Evolution, and Automated Repair with Large Language Models

---

## Overview

This repository provides the replication package for the AIWARE 2026 paper:

*Kubernetes Misconfigurations in the Wild: Taxonomy, Evolution, and Automated Repair with Large Language Models*

The objective of this package is to ensure transparency, reproducibility, and reusability of the results. It includes all datasets, scripts, and experimental artifacts required to reproduce the findings related to:

- RQ1: Taxonomy construction of Kubernetes misconfigurations  
- RQ2: Severity analysis across object types and categories  
- RQ3: Evolution of misconfigurations across project maturity  
- RQ4: LLM-based automated correction and schema-guided validation  

---

## Repository Structure

The repository is organized as follows:

.
├── data/            # Datasets used across all research questions  
├── taxonomy/        # Taxonomy definitions and mapping tables  
├── scripts/         # Data collection and preprocessing pipelines  
├── experiments/     # LLM prompts, configurations, and outputs  
├── kubecurity/      # Schema-guided correction framework  
├── results/         # Aggregated results and evaluation outputs  

---

## Reproducing the Results

### 1. Data Preparation
Datasets are available in the `data/` directory. They can also be regenerated using the provided scripts for:
- Stack Overflow extraction (RQ1)  
- GitHub Kubernetes configurations (RQ2, RQ4)  
- Helm charts (RQ3)  

### 2. Taxonomy Construction (RQ1)
- Run the BERTopic pipeline from `scripts/`  
- Reproduce hierarchical clustering and taxonomy generation  

### 3. Severity Analysis (RQ2)
- Execute misconfiguration detection tools:  
  - Datree  
  - Snyk  
  - KubeScore  
- Apply severity normalization and taxonomy mapping  

### 4. Evolution Analysis (RQ3)
- Compare incubator and stable Helm charts  
- Run analysis scripts to compute correction and emergence metrics  

### 5. LLM-Based Correction (RQ4)
- Use prompt templates from `experiments/`  
- Execute the four experimental settings  
- Evaluate outputs using:
  - Kubernetes schema validation  
  - Detection tools  

Optional:
- Enable the Kubecurity framework for schema-guided correction (Experiment 4)

---

## Requirements

- Python 3.10 or higher  
- Access to:
  - Stack Exchange API  
  - GitHub API  
- Kubernetes validation tools  
- Misconfiguration detection tools (Datree, Snyk, KubeScore)  

Optional:
- GPU for large-scale experiments  
- Access to LLM APIs or local models  

---

## Reproducibility Statement

All results reported in the paper can be reproduced using this package.  
Intermediate outputs and logs are provided to ensure:

- Full traceability  
- Deterministic evaluation  
- Independent verification  

---

## Extensibility

This replication package is designed to support further research. It can be extended to:

- Evaluate additional or more recent LLMs  
- Extend the taxonomy with new misconfiguration categories  
- Integrate alternative detection tools  
- Apply the methodology to other configuration systems  

---

## Citation

If you use this repository, please cite:

@inproceedings{ghorab2026aiware,
  title={Kubernetes Misconfigurations in the Wild: Taxonomy, Evolution, and Automated Repair with Large Language Models},
  author={Ghorab, Mostafa Anouar and Abdel Latif, Ahmad and Saied, Mohamed Aymen},
  booktitle={AIWARE 2026},
  year={2026}
}

---

## Contact

For questions or reproducibility issues, please open an issue in this repository.
