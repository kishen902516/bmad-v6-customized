#!/bin/bash

################################################################################
# Spring Boot Clean Architecture Generator - Module Registration Script
#
# Purpose: Register the Spring Boot Clean Arch module in BMAD manifests
# Author: BMad Builder Agent
# Date: 2025-11-07
# Version: 1.0.0
#
# This script automates the manual registration process for BMAD v6 alpha
# since the official custom module installer is not yet available.
#
# What it does:
#   1. Backs up existing manifest files
#   2. Adds module to manifest.yaml
#   3. Adds 4 agents to agent-manifest.csv
#   4. Adds 9 workflows to workflow-manifest.csv
#   5. Restores config.yaml from backup
#   6. Verifies registration
#   7. Displays summary
#
# Usage: ./register-module.sh [--dry-run]
#        --dry-run: Show what would be done without making changes
#
################################################################################

set -e  # Exit on error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Script configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
MODULE_NAME="spring-boot-clean-arch"
DRY_RUN=false

# File paths
MANIFEST_YAML="$PROJECT_ROOT/bmad/_cfg/manifest.yaml"
AGENT_MANIFEST="$PROJECT_ROOT/bmad/_cfg/agent-manifest.csv"
WORKFLOW_MANIFEST="$PROJECT_ROOT/bmad/_cfg/workflow-manifest.csv"
CONFIG_YAML="$PROJECT_ROOT/bmad/$MODULE_NAME/config.yaml"
CONFIG_BACKUP="$PROJECT_ROOT/bmad/$MODULE_NAME/config.yaml.backup"

# Parse arguments
for arg in "$@"; do
    case $arg in
        --dry-run)
            DRY_RUN=true
            shift
            ;;
        -h|--help)
            echo "Usage: $0 [--dry-run]"
            echo ""
            echo "Options:"
            echo "  --dry-run    Show what would be done without making changes"
            echo "  -h, --help   Show this help message"
            exit 0
            ;;
    esac
done

################################################################################
# Helper Functions
################################################################################

print_header() {
    echo ""
    echo -e "${CYAN}═══════════════════════════════════════════════════════════════${NC}"
    echo -e "${CYAN}  $1${NC}"
    echo -e "${CYAN}═══════════════════════════════════════════════════════════════${NC}"
    echo ""
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

check_file_exists() {
    if [ ! -f "$1" ]; then
        print_error "Required file not found: $1"
        exit 1
    fi
}

backup_file() {
    local file=$1
    local backup="${file}.backup-$(date +%Y%m%d-%H%M%S)"

    if [ "$DRY_RUN" = true ]; then
        print_info "Would backup: $file -> $backup"
    else
        cp "$file" "$backup"
        print_success "Backed up: $(basename $file)"
    fi
}

################################################################################
# Validation Functions
################################################################################

validate_environment() {
    print_header "Validating Environment"

    # Check we're in the right directory
    if [ ! -d "$PROJECT_ROOT/bmad" ]; then
        print_error "Not in BMAD project root. Please run from correct location."
        exit 1
    fi
    print_success "BMAD project root found"

    # Check module exists
    if [ ! -d "$PROJECT_ROOT/bmad/$MODULE_NAME" ]; then
        print_error "Module directory not found: bmad/$MODULE_NAME"
        exit 1
    fi
    print_success "Module directory found"

    # Check manifest files exist
    check_file_exists "$MANIFEST_YAML"
    check_file_exists "$AGENT_MANIFEST"
    check_file_exists "$WORKFLOW_MANIFEST"
    print_success "All manifest files found"

    # Check if already registered
    if grep -q "spring-boot-clean-arch" "$MANIFEST_YAML" 2>/dev/null; then
        print_warning "Module already registered in manifest.yaml"
        read -p "Continue anyway? (y/N) " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            print_info "Registration cancelled"
            exit 0
        fi
    fi

    # Check config backup exists
    if [ ! -f "$CONFIG_BACKUP" ]; then
        print_warning "Config backup not found: $CONFIG_BACKUP"
    else
        print_success "Config backup found"
    fi

    echo ""
    print_success "Environment validation complete"
}

################################################################################
# Registration Functions
################################################################################

register_in_manifest_yaml() {
    print_header "Registering in manifest.yaml"

    if [ "$DRY_RUN" = true ]; then
        print_info "Would add '$MODULE_NAME' to modules list"
        return
    fi

    # Backup first
    backup_file "$MANIFEST_YAML"

    # Check if already exists
    if grep -q "spring-boot-clean-arch" "$MANIFEST_YAML"; then
        print_warning "Module already in manifest.yaml - skipping"
        return
    fi

    # Add module to list (before the ides: line)
    sed -i '/^ides:/i\  - spring-boot-clean-arch' "$MANIFEST_YAML"

    print_success "Added module to manifest.yaml"
}

register_agents() {
    print_header "Registering Agents"

    # Agent data (CSV format with quoted fields)
    local agents=(
        "\"spring-architect\",\"Spring Architect\",\"Spring Architecture Agent\",\"🏛️\",\"Senior Software Architect\",\"Guide developers in creating Spring Boot applications that strictly follow Clean Architecture principles with automated enforcement and complete test coverage\",\"Professional but approachable; clear technical explanations; use examples and analogies; provide context for architectural decisions\",\"I guide with clarity and patience explaining the why behind architectural decisions. I provide options with trade-off analysis rather than dictating solutions. I celebrate successes and gently correct mistakes with constructive feedback.\",\"spring-boot-clean-arch\",\"bmad/spring-boot-clean-arch/agents/spring-architect.agent.yaml\""

        "\"code-generator\",\"Code Generator\",\"Code Generator Agent\",\"⚙️\",\"Senior Java Developer and Code Generation Specialist\",\"Generate production-quality Java code across all Clean Architecture layers leveraging Java 21 features optimally\",\"Precise and technically accurate; explain code generation decisions clearly; point out Java 21 features being used and why\",\"I am a precise and detail-oriented craftsperson who takes pride in generating clean well-structured code. I follow conventions meticulously and ensure every generated class is properly placed in the correct layer.\",\"spring-boot-clean-arch\",\"bmad/spring-boot-clean-arch/agents/code-generator.agent.yaml\""

        "\"test-engineer\",\"Test Engineer\",\"Test Engineer Agent\",\"🧪\",\"Senior Test Automation Engineer and Quality Architect\",\"Generate comprehensive maintainable test suites that provide confidence in code quality across all layers\",\"Quality-focused and detail-oriented; explain test coverage rationale; describe what each test validates; emphasize the value of different test types\",\"I am thorough quality-focused and believe that comprehensive testing is non-negotiable. I dont just generate tests - I generate meaningful tests that catch real bugs.\",\"spring-boot-clean-arch\",\"bmad/spring-boot-clean-arch/agents/test-engineer.agent.yaml\""

        "\"arch-validator\",\"Architecture Validator\",\"Architecture Validator Agent\",\"🛡️\",\"Architecture Compliance Officer and Quality Gatekeeper\",\"Enforce Clean Architecture principles automatically through ArchUnit validation detect violations early and guide developers toward compliant solutions\",\"Clear and precise about violations; explain the why behind each rule; provide actionable fix suggestions; celebrate compliance successes\",\"I am strict but fair - architectural rules exist for good reasons and I enforce them consistently. I dont just report violations; I explain why they matter and how to fix them.\",\"spring-boot-clean-arch\",\"bmad/spring-boot-clean-arch/agents/arch-validator.agent.yaml\""
    )

    if [ "$DRY_RUN" = true ]; then
        print_info "Would add ${#agents[@]} agents to agent-manifest.csv"
        for agent in "${agents[@]}"; do
            local name=$(echo "$agent" | cut -d',' -f1)
            print_info "  - $name"
        done
        return
    fi

    # Backup first
    backup_file "$AGENT_MANIFEST"

    # Add agents
    local added=0
    for agent in "${agents[@]}"; do
        local name=$(echo "$agent" | cut -d',' -f1)

        # Check if already exists
        if grep -q "\"$name\"" "$AGENT_MANIFEST"; then
            print_warning "Agent '$name' already exists - skipping"
            continue
        fi

        echo "$agent" >> "$AGENT_MANIFEST"
        print_success "Added agent: $name"
        added=$((added + 1))
    done

    print_success "Registered $added agents"
}

register_workflows() {
    print_header "Registering Workflows"

    # Workflow data (CSV format with quoted fields)
    local workflows=(
        "\"bootstrap-project\",\"Initialize new Spring Boot Clean Architecture project with Git Flow and GitHub Projects setup\",\"spring-boot-clean-arch\",\"bmad/spring-boot-clean-arch/workflows/bootstrap-project/workflow.yaml\",\"true\""

        "\"add-entity\",\"Add domain entity following TDD with automatic GitHub issue and feature branch\",\"spring-boot-clean-arch\",\"bmad/spring-boot-clean-arch/workflows/add-entity/workflow.yaml\",\"true\""

        "\"add-use-case\",\"Add application use case following TDD with mocked dependencies\",\"spring-boot-clean-arch\",\"bmad/spring-boot-clean-arch/workflows/add-use-case/workflow.yaml\",\"true\""

        "\"add-rest-endpoint\",\"Add REST API endpoint following TDD with Pact contract testing\",\"spring-boot-clean-arch\",\"bmad/spring-boot-clean-arch/workflows/add-rest-endpoint/workflow.yaml\",\"true\""

        "\"add-repository\",\"Add repository with interface in domain and JPA implementation in infrastructure\",\"spring-boot-clean-arch\",\"bmad/spring-boot-clean-arch/workflows/add-repository/workflow.yaml\",\"true\""

        "\"scaffold-feature\",\"Add complete feature across all layers with conversational guidance\",\"spring-boot-clean-arch\",\"bmad/spring-boot-clean-arch/workflows/scaffold-feature/workflow.yaml\",\"true\""

        "\"apply-pattern\",\"Apply design patterns (CQRS Event Sourcing Saga etc.) to your application\",\"spring-boot-clean-arch\",\"bmad/spring-boot-clean-arch/workflows/apply-pattern/workflow.yaml\",\"true\""

        "\"validate-architecture\",\"Run complete ArchUnit validation suite and check architectural compliance\",\"spring-boot-clean-arch\",\"bmad/spring-boot-clean-arch/workflows/validate-architecture/workflow.yaml\",\"true\""

        "\"generate-documentation\",\"Auto-generate README architecture diagrams and API documentation\",\"spring-boot-clean-arch\",\"bmad/spring-boot-clean-arch/workflows/generate-documentation/workflow.yaml\",\"true\""
    )

    if [ "$DRY_RUN" = true ]; then
        print_info "Would add ${#workflows[@]} workflows to workflow-manifest.csv"
        for workflow in "${workflows[@]}"; do
            local name=$(echo "$workflow" | cut -d',' -f1)
            print_info "  - $name"
        done
        return
    fi

    # Backup first
    backup_file "$WORKFLOW_MANIFEST"

    # Add workflows
    local added=0
    for workflow in "${workflows[@]}"; do
        local name=$(echo "$workflow" | cut -d',' -f1)

        # Check if already exists
        if grep -q "\"$name\".*spring-boot-clean-arch" "$WORKFLOW_MANIFEST"; then
            print_warning "Workflow '$name' already exists - skipping"
            continue
        fi

        echo "$workflow" >> "$WORKFLOW_MANIFEST"
        print_success "Added workflow: $name"
        added=$((added + 1))
    done

    print_success "Registered $added workflows"
}

restore_config() {
    print_header "Restoring Configuration"

    if [ ! -f "$CONFIG_BACKUP" ]; then
        print_warning "Config backup not found - skipping restore"
        return
    fi

    if [ -f "$CONFIG_YAML" ]; then
        print_info "Config already exists"
        return
    fi

    if [ "$DRY_RUN" = true ]; then
        print_info "Would restore: $CONFIG_BACKUP -> $CONFIG_YAML"
        return
    fi

    cp "$CONFIG_BACKUP" "$CONFIG_YAML"
    print_success "Restored config.yaml from backup"
}

################################################################################
# Verification Functions
################################################################################

verify_registration() {
    print_header "Verifying Registration"

    local success=true

    # Check manifest.yaml
    if grep -q "spring-boot-clean-arch" "$MANIFEST_YAML"; then
        print_success "Module found in manifest.yaml"
    else
        print_error "Module NOT found in manifest.yaml"
        success=false
    fi

    # Check agent count
    local agent_count=$(grep -c "spring-boot-clean-arch" "$AGENT_MANIFEST" || echo "0")
    if [ "$agent_count" -eq 4 ]; then
        print_success "All 4 agents found in agent-manifest.csv"
    else
        print_warning "Found $agent_count agents (expected 4)"
    fi

    # Check workflow count
    local workflow_count=$(grep -c "spring-boot-clean-arch" "$WORKFLOW_MANIFEST" || echo "0")
    if [ "$workflow_count" -eq 9 ]; then
        print_success "All 9 workflows found in workflow-manifest.csv"
    else
        print_warning "Found $workflow_count workflows (expected 9)"
    fi

    # Check config.yaml
    if [ -f "$CONFIG_YAML" ]; then
        print_success "config.yaml exists"
    else
        print_warning "config.yaml not found"
    fi

    echo ""
    if [ "$success" = true ]; then
        print_success "Verification complete - registration successful!"
    else
        print_warning "Verification complete - some issues detected"
    fi
}

################################################################################
# Main Execution
################################################################################

main() {
    print_header "Spring Boot Clean Arch - Module Registration"

    if [ "$DRY_RUN" = true ]; then
        print_warning "DRY RUN MODE - No changes will be made"
        echo ""
    fi

    print_info "Project Root: $PROJECT_ROOT"
    print_info "Module: $MODULE_NAME"
    echo ""

    # Validate environment
    validate_environment

    # Perform registration
    register_in_manifest_yaml
    register_agents
    register_workflows
    restore_config

    # Verify
    if [ "$DRY_RUN" = false ]; then
        verify_registration
    fi

    # Summary
    print_header "Registration Summary"

    if [ "$DRY_RUN" = true ]; then
        print_info "Dry run complete - no changes made"
        print_info "Run without --dry-run to perform actual registration"
    else
        print_success "Module registration complete!"
        echo ""
        print_info "What was done:"
        echo "  ✅ Added module to manifest.yaml"
        echo "  ✅ Registered 4 agents in agent-manifest.csv"
        echo "  ✅ Registered 9 workflows in workflow-manifest.csv"
        echo "  ✅ Restored config.yaml"
        echo "  ✅ Created backups of all modified files"
        echo ""
        print_info "Backups created with timestamp suffix: .backup-YYYYMMDD-HHMMSS"
        echo ""
        print_info "Next steps:"
        echo "  1. Verify slash commands work (if created)"
        echo "  2. Test agent loading"
        echo "  3. Test workflow execution"
        echo "  4. Update documentation"
    fi

    echo ""
    print_success "Script execution complete!"
    echo ""
}

# Run main function
main

exit 0
