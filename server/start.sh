#!/bin/bash
# PurrSkills Test Server Startup Script

cd "$(dirname "$0")"

echo "=== PurrSkills Test Server ==="
echo "Starting Paper 1.21.1..."
echo ""

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | grep -i version | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 21 ]; then
    echo "❌ ERROR: Java 21+ required, found Java $JAVA_VERSION"
    exit 1
fi

# Check if Paper JAR exists
if [ ! -f "paper.jar" ]; then
    echo "❌ ERROR: paper.jar not found!"
    echo "Run: wget -O paper.jar https://api.papermc.io/v2/projects/paper/versions/1.21.1/builds/132/downloads/paper-1.21.1-132.jar"
    exit 1
fi

# Check plugins
if [ ! -f "plugins/purrcore-1.0.0.jar" ] || [ ! -f "plugins/purrskills-1.0.0.jar" ]; then
    echo "❌ ERROR: Plugins missing in plugins/ folder!"
    exit 1
fi

echo "✅ Java $(java -version 2>&1 | head -1)"
echo "✅ Paper JAR ready"
echo "✅ Plugins: PurrCore, PurrSkills"
echo ""
echo "Server will start on port 25565 (offline mode)"
echo "Connect with Minecraft 1.21+ client to localhost:25565"
echo ""
echo "Press Ctrl+C to stop server"
echo "----------------------------------------"
echo ""

# Start server
exec java -Xmx2G -Xms2G -jar paper.jar nogui
