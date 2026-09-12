#!/usr/bin/env bash
# Quick Server Start Script

echo "🎮 Starting PurrSkills Test Server..."
echo ""
echo "Location: $(pwd)/server"
echo "Port: 25565 (localhost only)"
echo "Minecraft: 1.21.4 (Paper 26.2-123)"
echo "Plugins: PurrCore + PurrSkills"
echo ""

cd "$(dirname "$0")/server"

# Check if paper.jar exists
if [ ! -f "paper.jar" ]; then
    echo "❌ ERROR: paper.jar not found!"
    echo "The server files should be in: $(pwd)"
    exit 1
fi

echo "✅ Paper 26.2-123 JAR found (62 MB)"
echo "✅ Plugins ready (PurrCore + PurrSkills)"
echo "✅ Loading Java 25 via nix-shell..."
echo ""
echo "Starting server (this may take 30-60 seconds)..."
echo "----------------------------------------"
echo ""

# Start with nix-shell to get Java 25+ (required for Paper 26.2)
nix-shell -p openjdk25 --run "java -Xmx2G -Xms2G -jar paper.jar nogui"
