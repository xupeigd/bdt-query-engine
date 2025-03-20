package com.quicksand.bigdata.query.repos;

import com.quicksand.bigdata.query.dbvos.ConnectionInfoDBVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * ConnectionInfoAutoRepo
 *
 * @author xupei
 * @date 2022/8/11
 */
@Repository
public interface ConnectionInfoAutoRepo
        extends JpaRepository<ConnectionInfoDBVO, Integer> {

    ConnectionInfoDBVO findByFlag(String flag);

}
