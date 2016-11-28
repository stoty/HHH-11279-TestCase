package org.hibernate.bugs;

import static org.junit.Assert.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Hhh11279TestCase {

	private EntityManagerFactory entityManagerFactory;

	private static final String userEntityInsert = "INSERT INTO UserEntity (id, name) values (1 ,'test name') ";
    private static final String userDetailInsert = "INSERT INTO UserDetail (userId, detail) values (1 ,'test detail') ";

	@Before
	public void init() {
		entityManagerFactory = Persistence.createEntityManagerFactory( "templatePU" );
		
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		
		//Add a user and a detail record 
        entityManager.createNativeQuery(userEntityInsert).executeUpdate();
        entityManager.createNativeQuery(userDetailInsert).executeUpdate();
        
        entityManager.getTransaction().commit();
        entityManager.close();
	}

	@After
	public void destroy() {
		entityManagerFactory.close();
	}
	
	@Test
	public void hhh11279test() throws Exception {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		
		//Get the record directly from em
		UserDetail ud = entityManager.find(UserDetail.class, 1);
		
		//Everything is fine
		assertNotNull("em.find userDetail most not be null", ud);
        assertNotNull("em.find userDetail.getUser() most not be null", ud.getUser());
        assertEquals("em.find userDetail.getUser().getId() must be 1", 1, ud.getUser().getId());
		
        //Get the same record through a query
	    TypedQuery<UserDetail> q1 = entityManager.createNamedQuery("test1", UserDetail.class);
        ud = q1.getSingleResult();
        
        //Returned record has null Id
        assertNotNull("query result userDetail most not be null", ud);
        assertNotNull("query result userDetail.getUser() most not be null", ud.getUser());
        assertEquals("query result userDetail.getUser().getId() must be 1", 1 ,ud.getUser().getId());
	    
		entityManager.getTransaction().commit();
		entityManager.close();
		
	}
}
