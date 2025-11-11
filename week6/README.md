# MJT Olympics 🏃‍🏊‍🚴‍🏅

Welcome to the MJT Olympics project! This is a Java implementation of an Olympic Games management system that tracks competitors, competitions, and medal statistics.

## Project Structure

```
src/
└── bg/sofia/uni/fmi/mjt/olympics/
     ├── comparator/
     │     └── NationMedalComparator.java
     ├── competition/
     │     └── Competition.java
     ├── competitor/
     │     ├── Athlete.java
     │     ├── Competitor.java
     │     └── Medal.java
     ├── MJTOlympics.java
     └── Olympics.java

test/
└── bg/sofia/uni/fmi/mjt/olympics/
     ├── comparator/
     │     └── NationMedalComparatorTest.java
     ├── competition/
     │     └── CompetitionTest.java
     ├── competitor/
     │     └── AthleteTest.java
     └── MJTOlympicsTest.java
```

## Features

- Track competitors (athletes) with their personal details and medals
- Manage competitions with multiple participants
- Calculate and display medal statistics by nation
- Generate ranked lists of nations based on medal counts
- Comprehensive input validation and error handling

## Getting Started

### Prerequisites

- Java 17 or later
- Maven 3.6 or later

### Building the Project

```bash
mvn clean package
```

### Running Tests

```bash
mvn test
```

## Design Decisions

1. **Immutability**: Used immutable objects where possible (e.g., `Competition` as a record) and defensive copying to ensure thread safety.
2. **Encapsulation**: Properly encapsulated all class fields and provided controlled access through methods.
3. **Error Handling**: Comprehensive input validation with meaningful exception messages.
4. **Testing**: Comprehensive unit tests with JUnit 5 and Mockito.
5. **Documentation**: Thorough JavaDoc comments for all public APIs.

## Implementation Details

### Key Classes

- `Olympics` / `MJTOlympics`: Main interface and implementation for managing the Olympic Games.
- `Competitor` / `Athlete`: Interface and implementation for competitors in the games.
- `Competition`: Represents a competition with multiple participants.
- `Medal`: Enum representing the different types of medals (GOLD, SILVER, BRONZE).
- `NationMedalComparator`: Custom comparator for sorting nations by medal counts.

### Data Structures

- `Set<Competitor>`: Used to store registered competitors to ensure uniqueness.
- `Map<String, EnumMap<Medal, Integer>>`: Used to track medal counts by nation.
- `TreeSet<String>` with custom comparator: Used to maintain the ranked list of nations.

## Testing Strategy

- Unit tests for all classes with 100% line coverage.
- Edge cases and error conditions are thoroughly tested.
- Mock objects are used where appropriate to isolate components for testing.

## Future Improvements

1. Add support for team sports and team competitions.
2. Add persistence layer to save and load competition data.
3. Add more detailed statistics and reporting features.
4. Implement a REST API for remote access to the Olympics system.
5. Add a user interface for easier interaction with the system.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
