package xyz.tynn.buildsrc.publishing;

import com.android.build.gradle.LibraryExtension;
import com.android.build.gradle.api.LibraryVariant;

import org.gradle.api.Action;
import org.gradle.api.internal.DefaultDomainObjectSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("ConstantConditions")
class PluginActionTest {

    @Mock
    ProjectContext context;
    @Mock
    Action<LibraryVariant> variantAction;

    @Mock
    LibraryExtension extension;
    @Mock
    DefaultDomainObjectSet<LibraryVariant> variants;

    @InjectMocks
    PluginAction action;

    @Test
    void executeShouldRunVariantActionOnAllVariants() {
        doReturn(extension).when(context).getLibraryExtension();
        doReturn(variants).when(extension).getLibraryVariants();

        action.execute(null);

        verify(variants).all(variantAction);
        verify(extension).getLibraryVariants();
        verify(context).getLibraryExtension();
    }
}
