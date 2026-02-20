# Gradle wrapper
GRADLE = ./gradlew
GRADLE_FLAGS = --console=plain

# Directories
BUILD_DIR = build
LIBS_DIR = $(BUILD_DIR)/libs

# JAR files
QUEUE_JAR = $(LIBS_DIR)/s21_queue.jar
STACK_JAR = $(LIBS_DIR)/s21_stack.jar
GRAPH_JAR = $(LIBS_DIR)/graph.jar
ALGORITHMS_JAR = $(LIBS_DIR)/graph_algorithms.jar
APP_JAR = $(LIBS_DIR)/app.jar
FAT_JAR = $(LIBS_DIR)/SimpleNavigator-all.jar

.PHONY: all libs app clean rebuild run test info

all: libs app
	@echo "Build complete!"

libs:
	@echo "Building static libraries..."
	$(GRADLE) $(GRADLE_FLAGS) buildLibs

s21_queue.a:
	@echo "Building S21_queue library..."
	$(GRADLE) $(GRADLE_FLAGS) queueJar

s21_stack.a:
	@echo "Building S21_stack library..."
	$(GRADLE) $(GRADLE_FLAGS) stackJar

graph.a:
	@echo "Building Graph library..."
	$(GRADLE) $(GRADLE_FLAGS) graphJar

graph_algorithms.a:
	@echo "Building GraphAlgorithms library..."
	$(GRADLE) $(GRADLE_FLAGS) algorithmsJar


app: libs
	@echo "Building main application..."
	$(GRADLE) $(GRADLE_FLAGS) appJar

fat_jar:
	@echo "Building fat JAR..."
	$(GRADLE) $(GRADLE_FLAGS) fatJar

build_all:
	$(GRADLE) $(GRADLE_FLAGS) buildAll


run:
	$(GRADLE) $(GRADLE_FLAGS) run --quiet

run_jar: app
	@echo "Running from JAR files..."
	cd $(LIBS_DIR) && java -cp "s21_queue.jar:s21_stack.jar:graph.jar:graph_algorithms.jar:app.jar" Main

run_fat: fat_jar
	@echo "Running from fat JAR..."
	java -jar $(FAT_JAR)

test:
	@echo "Running tests..."
	$(GRADLE) $(GRADLE_FLAGS) test

clean:
	@echo "Cleaning build directory..."
	$(GRADLE) $(GRADLE_FLAGS) clean

clean_libs:
	@echo "Cleaning library JARs..."
	$(GRADLE) $(GRADLE_FLAGS) cleanLibs

rebuild: clean all