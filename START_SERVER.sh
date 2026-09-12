#!/usr/bin/env bash
# Quick Server Start Script

echo "🎮 Starting PurrSkills Test Server..."
echo ""
echo "Location: $(pwd)/server"
echo "Port: 25565 (localhost only)"
echo "Plugins: PurrCore + PurrSkills"
echo ""

cd "$(dirname "$0")/server"

# Check if paper.jar exists
if [ ! -f "paper.jar" ]; then
    echo "❌ ERROR: paper.jar not found!"
    echo "The server files should be in: $(pwd)"
    exit 1
fi

echo "✅ Paper JAR found"
echo "✅ Plugins ready"
echo ""
echo "Starting server..."
echo "----------------------------------------"
echo ""

# Start with nix-shell to get Java 21
nix-shell -p jdk21 --run "java -Xmx2G -Xms2G -jar paper.jar nogui"
