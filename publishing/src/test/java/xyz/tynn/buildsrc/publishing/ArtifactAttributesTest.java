package xyz.tynn.buildsrc.publishing;

import org.gradle.api.Action;
import org.gradle.api.attributes.AttributeContainer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ArtifactAttributesTest {

    @Mock
    AttributeContainer attributeContainer;
    @Mock
    Action<AttributeContainer> attributes1;
    @Mock
    Action<AttributeContainer> attributes2;

    @Test
    void executeShouldDelegateToAllAttributes() {
        ArtifactAttributes attributes = new ArtifactAttributes(asList(attributes1, attributes2));

        attributes.execute(attributeContainer);

        verify(attributes1).execute(attributeContainer);
        verify(attributes2).execute(attributeContainer);
    }
}
