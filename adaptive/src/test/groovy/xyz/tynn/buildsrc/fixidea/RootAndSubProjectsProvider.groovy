package xyz.tynn.buildsrc.fixidea

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider

import java.util.stream.Stream

import static org.junit.jupiter.params.provider.Arguments.arguments

class RootAndSubProjectsProvider implements ArgumentsProvider {

    @Override
    Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(arguments('rootProject'), arguments('subProject'))
    }
}
