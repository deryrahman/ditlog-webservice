package id.ac.itb.logistik.ditlog.repository;

import id.ac.itb.logistik.ditlog.model.SPMKContract;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SPMKContractRepository extends CrudRepository<SPMKContract, Long> {

    @Query(value = "SELECT * FROM V_SPMK_ANDROID WHERE ID_KONTRAK = ?1", nativeQuery = true)
    SPMKContract findContractById(Long id);
    @Query(value = "SELECT * FROM V_SPMK_ANDROID WHERE TAHUN = ?1", nativeQuery = true)
    Iterable<SPMKContract> findAllByYear(Long year);
}