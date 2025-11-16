#!/bin/bash

# Increment version script for Metal Shader Support Plugin
# © 2025 Jakub Jirák

set -e

VERSION_FILE="gradle.properties"

# Read current version
CURRENT_VERSION=$(grep "pluginVersion=" $VERSION_FILE | cut -d'=' -f2)

echo "Current version: $CURRENT_VERSION"

# Parse version
IFS='.' read -r -a VERSION_PARTS <<< "$CURRENT_VERSION"
MAJOR="${VERSION_PARTS[0]}"
MINOR="${VERSION_PARTS[1]}"
PATCH="${VERSION_PARTS[2]}"

# Increment patch version
PATCH=$((PATCH + 1))
NEW_VERSION="$MAJOR.$MINOR.$PATCH"

echo "New version: $NEW_VERSION"

# Update gradle.properties
sed -i '' "s/pluginVersion=.*/pluginVersion=$NEW_VERSION/" $VERSION_FILE

echo "✅ Version incremented to $NEW_VERSION"
echo ""
echo "Next steps:"
echo "1. Review changes"
echo "2. Run: ./gradlew buildPlugin"
echo "3. Test the plugin"
echo "4. Commit: git commit -am 'Release v$NEW_VERSION'"
echo "5. Tag: git tag v$NEW_VERSION"
