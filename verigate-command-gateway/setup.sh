#!/bin/bash

# Verigate Verification Platform Setup Script
# This script sets up the development environment for the Verigate project

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Helper functions
print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_header() {
    echo -e "\n${BLUE}=== $1 ===${NC}"
}

# Check if running on macOS
check_macos() {
    if [[ "$OSTYPE" != "darwin"* ]]; then
        print_warning "This script is optimized for macOS. Some steps may need manual adjustment on other platforms."
    fi
}

# Check if Homebrew is installed
check_homebrew() {
    if ! command -v brew &> /dev/null; then
        print_error "Homebrew is not installed. Please install it first:"
        echo "  /bin/bash -c \"\$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)\""
        exit 1
    fi
    print_success "Homebrew is installed"
}

# Install required dependencies
install_dependencies() {
    print_header "Installing Dependencies"
    
    # Install jq for JSON processing
    if ! command -v jq &> /dev/null; then
        print_info "Installing jq..."
        brew install jq
        print_success "jq installed"
    else
        print_success "jq is already installed"
    fi
    
    # Install Docker if not present
    if ! command -v docker &> /dev/null; then
        print_info "Installing Docker..."
        brew install --cask docker
        print_success "Docker installed"
        print_warning "Please start Docker Desktop manually after setup completes"
    else
        print_success "Docker is already installed"
    fi
    
    # Install Colima for macOS (Docker alternative)
    if [[ "$OSTYPE" == "darwin"* ]]; then
        if ! command -v colima &> /dev/null; then
            print_info "Installing Colima..."
            brew install colima
            print_success "Colima installed"
        else
            print_success "Colima is already installed"
        fi
    fi
    
    # Check Java installation
    if ! command -v java &> /dev/null; then
        print_warning "Java is not installed. Please install Java 17 or higher:"
        echo "  brew install openjdk@17"
        echo "  sudo ln -sfn $(brew --prefix)/opt/openjdk@17/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk"
    else
        JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
        if [[ "$JAVA_VERSION" -ge 17 ]]; then
            print_success "Java $JAVA_VERSION is installed"
        else
            print_warning "Java version is $JAVA_VERSION, but Java 17+ is recommended for this project"
        fi
    fi
    
    # Check Maven installation
    if ! command -v mvn &> /dev/null; then
        print_info "Installing Maven..."
        brew install maven
        print_success "Maven installed"
    else
        print_success "Maven is already installed"
    fi
}

# Setup Maven settings.xml for GitHub Package Registry
setup_maven_settings() {
    print_header "Setting up Maven Configuration"
    
    local settings_file="$HOME/.m2/settings.xml"
    
    # Create .m2 directory if it doesn't exist
    mkdir -p "$HOME/.m2"
    
    if [[ -f "$settings_file" ]]; then
        print_warning "Maven settings.xml already exists at $settings_file"
        read -p "Do you want to backup and replace it? (y/N): " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            cp "$settings_file" "$settings_file.backup.$(date +%Y%m%d_%H%M%S)"
            print_success "Backed up existing settings.xml"
        else
            print_info "Skipping Maven settings.xml setup"
            return
        fi
    fi
    
    print_info "Creating Maven settings.xml template..."
    
    cat > "$settings_file" << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                              http://maven.apache.org/xsd/settings-1.0.0.xsd">
  <servers>
    <server>
      <id>github</id>
      <username>YOUR_GITHUB_USERNAME</username>
      <password>YOUR_GITHUB_PERSONAL_ACCESS_TOKEN</password>
    </server>
  </servers>
</settings>
EOF
    
    print_success "Maven settings.xml template created at $settings_file"
    print_warning "IMPORTANT: You must update the GitHub credentials in $settings_file"
    echo ""
    print_info "To complete the setup:"
    echo "  1. Create a GitHub Personal Access Token with 'read:packages' scope"
    echo "     https://github.com/settings/tokens/new?scopes=read:packages"
    echo "  2. Edit $settings_file"
    echo "  3. Replace YOUR_GITHUB_USERNAME with your GitHub username"
    echo "  4. Replace YOUR_GITHUB_PERSONAL_ACCESS_TOKEN with your token"
}

# Setup Docker environment
setup_docker() {
    print_header "Setting up Docker Environment"
    
    if [[ "$OSTYPE" == "darwin"* ]] && command -v colima &> /dev/null; then
        print_info "Starting Colima (if not already running)..."
        if ! colima status &> /dev/null; then
            colima start
            print_success "Colima started"
        else
            print_success "Colima is already running"
        fi
    fi
    
    # Check if Docker is accessible
    if docker info &> /dev/null; then
        print_success "Docker is running and accessible"
    else
        print_warning "Docker is not running. Please start Docker Desktop or Colima manually"
    fi
}

# Validate project setup
validate_setup() {
    print_header "Validating Project Setup"
    
    print_info "Testing Maven dependency resolution..."
    if mvn dependency:resolve -q; then
        print_success "Maven dependencies resolved successfully"
    else
        print_error "Maven dependency resolution failed"
        print_info "This is likely due to missing GitHub Package Registry credentials"
        print_info "Please complete the Maven settings.xml configuration as described above"
        return 1
    fi
    
    print_info "Running quick compilation test..."
    if mvn clean compile -q; then
        print_success "Project compiles successfully"
    else
        print_error "Project compilation failed"
        return 1
    fi
    
    return 0
}

# Main setup function
main() {
    print_header "Verigate Development Environment Setup"
    echo "This script will set up your development environment for the Verigate verification platform."
    echo ""
    
    check_macos
    check_homebrew
    install_dependencies
    setup_maven_settings
    setup_docker
    
    echo ""
    print_header "Setup Complete!"
    print_success "Basic setup is complete"
    
    echo ""
    print_info "Next steps:"
    echo "  1. Update your Maven settings.xml with GitHub credentials (see instructions above)"
    echo "  2. Run 'mvn clean install' to build the project"
    echo "  3. Run 'mvn test' to execute the test suite"
    echo ""
    
    print_info "Optional: Test the setup with validation"
    read -p "Do you want to run validation tests now? (requires GitHub credentials to be configured) (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        if validate_setup; then
            print_success "🎉 Setup validation passed! Your environment is ready for development."
        else
            print_warning "Setup validation failed. Please complete the GitHub credentials configuration."
        fi
    fi
    
    echo ""
    print_info "For more information, see README.md and CLAUDE.md"
}

# Run main function
main "$@"