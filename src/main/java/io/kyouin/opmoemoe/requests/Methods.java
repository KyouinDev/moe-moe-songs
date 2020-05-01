package io.kyouin.opmoemoe.requests;

import io.kyouin.opmoemoe.entities.MoeEntry;
import io.kyouin.opmoemoe.entities.MoeEntryDetails;
import io.kyouin.opmoemoe.utils.MoeConstants;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;

public interface Methods {

    @GET(MoeConstants.DETAILS_PATH)
    Call<MoeEntryDetails> getDetails(@Query(value = "file", encoded = true) String filename);

    @GET(MoeConstants.LIST_PATH)
    Call<List<String>> getFilenames(@Query("shuffle") Boolean shuffle,
                                    @Query("filenames") Boolean filenames,
                                    @Query(value = "first", encoded = true) String firstFilename);

    @GET(MoeConstants.LIST_PATH)
    Call<List<MoeEntry>> getResults(@Query("shuffle") Boolean shuffle,
                                    @Query(value = "first", encoded = true) String firstFilename);
}
